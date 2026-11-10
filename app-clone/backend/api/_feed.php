<?php
/**
 * Shared list endpoints. Each public php file sets $FEED or $MODE then requires this file.
 *  - $FEED = feed key (govtjob|currentaffairs|resultpost|dailynews)
 *  - $MODE = 'plain' | 'expired' | 'byfilter'
 */
declare(strict_types=1);
require_once __DIR__ . '/config.php';

cors();

try {
    $feed = $FEED ?? null;
    $mode = $MODE ?? 'plain';

    if ($mode === 'byfilter') {
        $labelId = p_str('labelid');
        $group   = p_str('filter');
        if ($labelId === '') {
            fail('labelid is required');
        }
        $sql  = 'SELECT p.* FROM posts p WHERE p.is_deleted = 0 AND p.feed = "govtjob"';
        $args = [];
        // Constrain by the selected filter label when it resolves to a known group.
        if ($group !== '' && in_array($group, ROZGARONE_GROUPS, true)) {
            $sql .= ' AND EXISTS (SELECT 1 FROM filters f WHERE f.id = ? AND f.group_name = ?)';
            $args[] = (int) $labelId;
            $args[] = $group;
        }
        $sql .= ' ORDER BY p.publish_date DESC, p.id DESC LIMIT 300';
        $stmt = db()->prepare($sql);
        $stmt->execute($args);
        ok('govtjob', array_map('shape_post', $stmt->fetchAll()));
    }

    if ($feed === null || !in_array($feed, ROZGARONE_FEEDS, true)) {
        fail('unknown feed');
    }

    $where  = 'is_deleted = 0 AND feed = ?';
    $args   = [$feed];
    $key    = $feed;

    if ($feed === 'govtjob') {
        $where .= $mode === 'expired' ? ' AND is_expired = 1' : ' AND is_expired = 0';
        $key    = 'govtjob';
    }

    $stmt = db()->prepare("SELECT * FROM posts WHERE {$where} ORDER BY publish_date DESC, id DESC LIMIT 300");
    $stmt->execute($args);
    ok($key, array_map('shape_post', $stmt->fetchAll()));
} catch (Throwable $e) {
    fail('Server error: ' . $e->getMessage());
}
