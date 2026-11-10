<?php
/**
 * GET api/getfilter.php?title=Category|Profession|Qualification|Location
 * → { status, message, filter: [{ category_name, id }] }
 * The Android Category screen passes the group title as an intent extra and
 * requests exactly that group; without a title every group is returned.
 */
declare(strict_types=1);
require_once __DIR__ . '/config.php';

cors();

try {
    $title = p_str('title', p_str('group'));

    if ($title !== '' && in_array($title, SARKAR_GROUPS, true)) {
        $stmt = db()->prepare(
            'SELECT id, category_name FROM filters WHERE group_name = ? ORDER BY category_name ASC'
        );
        $stmt->execute([$title]);
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
