<?php
/**
 * admin_api/notifications.php — CRUD for notification banners.
 *   GET  → { notifications:[{id, maintitle, secondtitle, publishdate}] }
 *   POST action=create|update|delete   fields: maintitle, secondtitle, publishdate
 */
declare(strict_types=1);
require_once __DIR__ . '/_bootstrap.php';

require_auth();

try {
    if (($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'GET') {
        $stmt = db()->query('SELECT * FROM notifications ORDER BY publish_date DESC, id DESC LIMIT 200');
        $out  = [];
        foreach ($stmt->fetchAll() as $r) {
            $out[] = [
                'id'          => (int) $r['id'],
                'maintitle'   => (string) $r['main_title'],
                'secondtitle' => (string) $r['second_title'],
                'publishdate' => (string) $r['publish_date'],
            ];
        }
        admin_json(['notifications' => $out]);
    }

    require_method('POST');
    check_csrf();
    $action = p_str('action', 'create');

    if ($action === 'delete') {
        $id = p_int('id');
        if ($id <= 0) {
            admin_error('id is required');
        }
        db()->prepare('DELETE FROM notifications WHERE id = ?')->execute([$id]);
        admin_json(['message' => 'Notification deleted']);
    }

    $main = p_str('maintitle');
    $sub  = p_str('secondtitle');
    $date = substr(p_str('publishdate', date('Y-m-d')), 0, 10);
    if ($main === '') {
        admin_error('maintitle is required');
    }

    if ($action === 'create') {
        db()->prepare('INSERT INTO notifications (main_title, second_title, publish_date) VALUES (?, ?, ?)')
            ->execute([$main, $sub, $date]);
        admin_json(['message' => 'Notification created', 'id' => (int) db()->lastInsertId()]);
    }

    if ($action === 'update') {
        $id = p_int('id');
        if ($id <= 0) {
            admin_error('id is required');
        }
        db()->prepare('UPDATE notifications SET main_title = ?, second_title = ?, publish_date = ? WHERE id = ?')
            ->execute([$main, $sub, $date, $id]);
        admin_json(['message' => 'Notification updated', 'id' => $id]);
    }

    admin_error('Unknown action');
} catch (Throwable $e) {
    admin_error('Server error: ' . $e->getMessage(), 500);
}
