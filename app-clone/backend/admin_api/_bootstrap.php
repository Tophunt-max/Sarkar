<?php
/**
 * Admin API bootstrap: session, auth guard, JSON in/out.
 * Include from every admin_api/*.php file.
 */
declare(strict_types=1);

require_once dirname(__DIR__) . '/config.php';

if (session_status() !== PHP_SESSION_ACTIVE) {
    session_set_cookie_params([
        'httponly' => true,
        'samesite' => 'Lax',
        'path'     => '/',
    ]);
    session_name('sarkar_admin');
    session_start();
}

function current_user(): ?array
{
    return $_SESSION['admin'] ?? null;
}

/** Reject the request unless an admin session exists. */
function require_auth(): array
{
    $u = current_user();
    if ($u === null) {
        json_out(['status' => 'false', 'message' => 'Unauthorized'], 401);
    }
    return $u;
}

function admin_json(array $payload): void
{
    json_out(['status' => 'true'] + $payload);
}

function admin_error(string $message, int $code = 400): void
{
    json_out(['status' => 'false', 'message' => $message], $code);
}

/** Only allow POST (optionally restrict method). */
function require_method(string $method = 'POST'): void
{
    if (($_SERVER['REQUEST_METHOD'] ?? 'GET') !== $method) {
        admin_error('Method not allowed', 405);
    }
}

function csrf_token(): string
{
    if (empty($_SESSION['csrf'])) {
        $_SESSION['csrf'] = bin2hex(random_bytes(16));
    }
    return $_SESSION['csrf'];
}

function check_csrf(): void
{
    $sent = $_SERVER['HTTP_X_CSRF_TOKEN'] ?? p_str('csrf');
    if (!is_string($sent) || ($_SESSION['csrf'] ?? '') === '' || !hash_equals($_SESSION['csrf'], $sent)) {
        admin_error('Invalid CSRF token', 419);
    }
}
