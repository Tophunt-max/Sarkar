<?php
/**
 * admin_api/settings.php — AdMob ids + admin password change.
 *   GET  → { settings:{admob_banner, admob_interstitial, admob_native, admob_appopen} }
 *   POST action=save|password
 */
declare(strict_types=1);
require_once __DIR__ . '/_bootstrap.php';

$user = require_auth();

const SARKAR_KEYS = ['admob_banner', 'admob_interstitial', 'admob_native', 'admob_appopen'];

try {
    if (($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'GET') {
        $stmt = db()->query('SELECT setting_key, setting_value FROM app_settings');
        $map  = [];
        foreach ($stmt->fetchAll() as $r) {
            $map[$r['setting_key']] = $r['setting_value'];
        }
        $settings = [];
        foreach (SARKAR_KEYS as $k) {
            $settings[$k] = $map[$k] ?? '';
        }
        admin_json(['settings' => $settings]);
    }

    require_method('POST');
    check_csrf();
    $action = p_str('action', 'save');

    if ($action === 'save') {
        $stmt = db()->prepare(
            'INSERT INTO app_settings (setting_key, setting_value) VALUES (?, ?)
             ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)'
        );
        foreach (SARKAR_KEYS as $k) {
            $v = p_str($k);
            if ($v !== '') {
                $stmt->execute([$k, $v]);
            }
        }
        admin_json(['message' => 'Settings saved']);
    }

    if ($action === 'password') {
        $current = p_str('current_password');
        $new      = p_str('new_password');
        if (strlen($new) < 6) {
            admin_error('New password must be at least 6 characters');
        }
        $stmt = db()->prepare('SELECT * FROM admin_users WHERE id = ? LIMIT 1');
        $stmt->execute([$user['id']]);
        $row = $stmt->fetch();
        if (!$row || !password_verify($current, $row['password_hash'])) {
            admin_error('Current password is incorrect');
        }
        db()->prepare('UPDATE admin_users SET password_hash = ? WHERE id = ?')
            ->execute([password_hash($new, PASSWORD_BCRYPT), $user['id']]);
        admin_json(['message' => 'Password updated']);
    }

    admin_error('Unknown action');
} catch (Throwable $e) {
    admin_error('Server error: ' . $e->getMessage(), 500);
}
