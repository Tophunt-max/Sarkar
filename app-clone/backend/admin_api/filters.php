<?php
/**
 * admin_api/filters.php — CRUD for filter labels (Category/Profession/Qualification/Location).
 *   GET  ?group=           → { filters:[{id, group_name, category_name}] }
 *   POST action=create|update|delete
 */
declare(strict_types=1);
require_once __DIR__ . '/_bootstrap.php';

require_auth();

try {
    if (($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'GET') {
        $group = p_str('group');
        if (in_array($group, ROZGARONE_GROUPS, true)) {
            $stmt = db()->prepare('SELECT * FROM filters WHERE group_name = ? ORDER BY category_name ASC');
            $stmt->execute([$group]);
        } else {
            $stmt = db()->query('SELECT * FROM filters ORDER BY group_name ASC, category_name ASC');
        }
        $out = [];
        foreach ($stmt->fetchAll() as $r) {
            $out[] = [
                'id'            => (int) $r['id'],
                'group_name'    => (string) $r['group_name'],
                'category_name' => (string) $r['category_name'],
            ];
        }
        admin_json(['filters' => $out]);
    }

    require_method('POST');
    check_csrf();
    $action = p_str('action', 'create');

    if ($action === 'delete') {
        $id = p_int('id');
        if ($id <= 0) {
            admin_error('id is required');
        }
        db()->prepare('DELETE FROM filters WHERE id = ?')->execute([$id]);
        admin_json(['message' => 'Filter deleted']);
    }

    $group = p_str('group_name', p_str('group'));
    $name  = p_str('category_name', p_str('name'));
    if (!in_array($group, ROZGARONE_GROUPS, true)) {
        admin_error('Invalid group_name');
    }
    if ($name === '') {
        admin_error('category_name is required');
    }

    if ($action === 'create') {
        db()->prepare('INSERT INTO filters (group_name, category_name) VALUES (?, ?)')->execute([$group, $name]);
        admin_json(['message' => 'Filter created', 'id' => (int) db()->lastInsertId()]);
    }

    if ($action === 'update') {
        $id = p_int('id');
        if ($id <= 0) {
            admin_error('id is required');
        }
        db()->prepare('UPDATE filters SET group_name = ?, category_name = ? WHERE id = ?')
            ->execute([$group, $name, $id]);
        admin_json(['message' => 'Filter updated', 'id' => $id]);
    }

    admin_error('Unknown action');
} catch (Throwable $e) {
    admin_error('Server error: ' . $e->getMessage(), 500);
}
