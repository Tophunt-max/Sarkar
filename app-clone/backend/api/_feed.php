<?php
/**
 * Shared list endpoints. Each public php file sets $FEED or $MODE then requires this file.
 *  - $FEED = feed key (govtjob|currentaffairs|resultpost|dailynews)
 *  - $MODE = 'plain' | 'expired' | 'byfilter'
 */
declare(strict_types=1);
require_once dirname(__DIR__) . '/config.php';

cors();

try {
    $feed = $FEED ?? null;
    $mode = $MODE ?? 'plain';

    if ($mode === 'byfilter') {
        // Form fields sent by CategoryContent: labelid = filter[].id, filter = group title.
        $labelId = p_int('labelid');
        $group   = p_str('filter');
        if ($labelId <= 0) {
            fail('labelid is required');
        }
        // Active govt-job posts tagged with this label (post_labels links posts to filters).
        $sql = 'SELECT p.* FROM posts p
                  JOIN post_labels pl ON pl.post_id = p.id
                  JOIN filters f      ON f.id = pl.filter_id
                 WHERE p.is_deleted = 0 AND p.is_expired = 0 AND p.feed = \'govtjob\' AND f.id = ?';
        $args = [$labelId];
        if ($group !== '' && in_array($group, ROZGARONE_GROUPS, true)) {
            $sql   .= ' AND f.group_name = ?';
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
