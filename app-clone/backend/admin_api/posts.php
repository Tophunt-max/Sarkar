<?php
/**
 * admin_api/posts.php — CRUD for feed posts.
 *   GET                       → { posts:[...], total, page, per_page, feeds:{govtjob:n,...} }
 *      ?feed=  ?search=  ?page=  ?per_page=  ?scope=active|expired|deleted
 *   POST action=create|update|delete|restore   (update/delete need id)
 */
declare(strict_types=1);
require_once __DIR__ . '/_bootstrap.php';

require_auth();

function post_payload(): array
{
    $feed  = p_str('feed');
    $title = p_str('jobTitle', p_str('job_title'));
    $body  = p_str('jobContent', p_str('job_content'));
    if (!in_array($feed, ROZGARONE_FEEDS, true)) {
        admin_error('Invalid feed');
    }
    if ($title === '') {
        admin_error('jobTitle is required');
    }
    $publish = p_str('postPublishDate', p_str('publish_date'));
    if ($publish === '') {
        $publish = date('Y-m-d');
    }
    return [
        'postid'          => p_str('postid') !== '' ? p_str('postid') : 'SRK' . time() . random_int(100, 999),
        'feed'            => $feed,
        'job_title'       => $title,
        'job_content'     => $body,
        'publish_date'    => substr($publish, 0, 10),
        'formatted_date'  => p_str('formattedDate', p_str('formatted_date')) !== ''
                                ? p_str('formattedDate', p_str('formatted_date'))
                                : date('d M Y', strtotime($publish)),
        'expired_date'    => p_str('expiredDate', p_str('expired_date')) !== null
                                ? p_str('expiredDate', p_str('expired_date')) : '',
        'is_expired'      => p_int('isExpired', p_int('is_expired')) ? 1 : 0,
    ];
}

function row_to_admin(array $r): array
{
    return [
        'id'             => (int) $r['id'],
        'postid'         => (string) $r['postid'],
        'feed'           => (string) $r['feed'],
        'jobTitle'       => (string) $r['job_title'],
        'jobContent'     => (string) $r['job_content'],
        'postPublishDate'=> (string) $r['publish_date'],
        'formattedDate'  => (string) $r['formatted_date'],
        'expiredDate'    => (string) ($r['expired_date'] ?? ''),
        'isExpired'      => (int) $r['is_expired'],
        'isDeleted'      => (int) $r['is_deleted'],
        'updatedAt'      => (string) $r['updated_at'],
    ];
}

try {
    $method = $_SERVER['REQUEST_METHOD'] ?? 'GET';

    if ($method === 'GET') {
        $feed   = p_str('feed');
        $search = p_str('search');
        $scope  = p_str('scope', 'active');
        $page   = max(1, p_int('page', 1));
        $per    = min(100, max(5, p_int('per_page', 15)));
        $offset = ($page - 1) * $per;

        $where  = [];
        $args   = [];
        if (in_array($feed, ROZGARONE_FEEDS, true)) {
            $where[] = 'feed = ?';
            $args[]  = $feed;
        }
        if ($scope === 'expired') {
            $where[] = 'is_expired = 1 AND is_deleted = 0';
        } elseif ($scope === 'deleted') {
            $where[] = 'is_deleted = 1';
        } else {
            $where[] = 'is_deleted = 0';
        }
        if ($search !== '') {
            $where[] = 'job_title LIKE ?';
            $args[]  = '%' . $search . '%';
        }
        $sqlWhere = $where ? 'WHERE ' . implode(' AND ', $where) : '';

        $cnt = db()->prepare("SELECT COUNT(*) FROM posts {$sqlWhere}");
        $cnt->execute($args);
        $total = (int) $cnt->fetchColumn();

        $stmt = db()->prepare(
            "SELECT * FROM posts {$sqlWhere} ORDER BY publish_date DESC, id DESC LIMIT {$per} OFFSET {$offset}"
        );
        $stmt->execute($args);
        $posts = array_map('row_to_admin', $stmt->fetchAll());

        $counts = ['govtjob' => 0, 'currentaffairs' => 0, 'resultpost' => 0, 'dailynews' => 0];
        foreach (db()->query('SELECT feed, COUNT(*) c FROM posts WHERE is_deleted = 0 GROUP BY feed') as $r) {
            $counts[$r['feed']] = (int) $r['c'];
        }

        admin_json([
            'posts'    => $posts,
            'total'    => $total,
            'page'     => $page,
            'per_page' => $per,
            'feeds'    => $counts,
        ]);
    }

    require_method('POST');
    check_csrf();
    $action = p_str('action', 'create');

    if ($action === 'create') {
        $d = post_payload();
        $stmt = db()->prepare(
            'INSERT INTO posts (postid, feed, job_title, job_content, publish_date, formatted_date, expired_date, is_expired)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $d['postid'], $d['feed'], $d['job_title'], $d['job_content'],
            $d['publish_date'], $d['formatted_date'], $d['expired_date'], $d['is_expired'],
        ]);
        admin_json(['message' => 'Post created', 'id' => (int) db()->lastInsertId()]);
    }

    if (in_array($action, ['update', 'delete', 'restore'], true)) {
        $id = p_int('id');
        if ($id <= 0) {
            admin_error('id is required');
        }
        if ($action === 'delete') {
            db()->prepare('UPDATE posts SET is_deleted = 1 WHERE id = ?')->execute([$id]);
            admin_json(['message' => 'Post deleted']);
        }
        if ($action === 'restore') {
            db()->prepare('UPDATE posts SET is_deleted = 0 WHERE id = ?')->execute([$id]);
            admin_json(['message' => 'Post restored']);
        }
        $d = post_payload();
        db()->prepare(
            'UPDATE posts SET postid=?, feed=?, job_title=?, job_content=?, publish_date=?,
                    formatted_date=?, expired_date=?, is_expired=?, is_deleted=0 WHERE id=?'
        )->execute([
            $d['postid'], $d['feed'], $d['job_title'], $d['job_content'],
            $d['publish_date'], $d['formatted_date'], $d['expired_date'], $d['is_expired'], $id,
        ]);
        admin_json(['message' => 'Post updated', 'id' => $id]);
    }

    admin_error('Unknown action');
} catch (Throwable $e) {
    admin_error('Server error: ' . $e->getMessage(), 500);
}
