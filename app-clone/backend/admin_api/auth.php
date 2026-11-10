<?php
/** POST admin_api/auth.php?action=login|logout ; GET ?action=me */
declare(strict_types=1);
require_once __DIR__ . '/_bootstrap.php';

$action = $_GET['action'] ?? p_str('action', 'me');

try {
    if ($action === 'login') {
        require_method('POST');
        $username = p_str('username');
        $password = p_str('password');
        if ($username === '' || $password === '') {
            admin_error('Username and password are required');
        }
        $stmt = db()->prepare('SELECT * FROM admin_users WHERE username = ? LIMIT 1');
        $stmt->execute([$username]);
        $user = $stmt->fetch();
        if (!$user || !password_verify($password, $user['password_hash'])) {
            admin_error('Invalid credentials', 401);
        }
        session_regenerate_id(true);
        $_SESSION['admin'] = [
            'id'       => (int) $user['id'],
            'username' => (string) $user['username'],
            'name'     => (string) $user['name'],
        ];
        admin_json(['message' => 'Logged in', 'user' => $_SESSION['admin'], 'csrf' => csrf_token()]);
    }

    if ($action === 'logout') {
        require_method('POST');
        $_SESSION = [];
        session_destroy();
        admin_json(['message' => 'Logged out']);
    }

    if ($action === 'me') {
        $u = current_user();
        if ($u === null) {
            admin_error('Unauthorized', 401);
        }
        admin_json(['user' => $u, 'csrf' => csrf_token()]);
    }

    admin_error('Unknown action');
} catch (Throwable $e) {
    admin_error('Server error: ' . $e->getMessage(), 500);
}
