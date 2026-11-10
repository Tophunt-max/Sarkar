<?php
/**
 * POST|GET api/getfilter.php  filter=Category|Profession|Qualification|Location
 * → { status, message, filter: [{ category_name, id }] }
 * The Android Category screen sends its group title as the "filter" form field
 * (REA: Category.T() puts map("filter", title)). Without a valid group every value is returned.
 */
declare(strict_types=1);
require_once dirname(__DIR__) . '/config.php';

cors();

try {
    $group = p_str('filter', p_str('title', p_str('group')));

    if ($group !== '' && in_array($group, ROZGARONE_GROUPS, true)) {
        $stmt = db()->prepare(
            'SELECT id, category_name FROM filters WHERE group_name = ? ORDER BY category_name ASC'
        );
        $stmt->execute([$group]);
    } else {
        $stmt = db()->query('SELECT id, category_name FROM filters ORDER BY group_name ASC, category_name ASC');
    }

    $rows = [];
    foreach ($stmt->fetchAll() as $r) {
        $rows[] = ['category_name' => (string) $r['category_name'], 'id' => (string) $r['id']];
    }
    ok('filter', $rows);
} catch (Throwable $e) {
    fail('Server error: ' . $e->getMessage());
}
