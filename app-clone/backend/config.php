<?php
/**
 * RozgarOne — shared bootstrap: DB connection, JSON helpers, CORS.
 * Override credentials with env vars ROZGARONE_DB_HOST / _NAME / _USER / _PASS.
 */
declare(strict_types=1);

const ROZGARONE_FEEDS = ['govtjob', 'currentaffairs', 'resultpost', 'dailynews'];
const ROZGARONE_GROUPS = ['Category', 'Profession', 'Qualification', 'Location'];

function db(): PDO
{
    static $pdo = null;
    if ($pdo instanceof PDO) {
        return $pdo;
    }
    $host = getenv('ROZGARONE_DB_HOST') ?: '127.0.0.1';
    $name = getenv('ROZGARONE_DB_NAME') ?: 'rozgarone';
    $user = getenv('ROZGARONE_DB_USER') ?: 'root';
    $pass = getenv('ROZGARONE_DB_PASS') ?: '';
    $dsn  = "mysql:host={$host};dbname={$name};charset=utf8mb4";
    $pdo  = new PDO($dsn, $user, $pass, [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ]);
    return $pdo;
}

function json_out(array $payload, int $code = 200): void
{
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    header('Cache-Control: no-store');
    echo json_encode($payload, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
    exit;
}

function ok(string $key, array $rows): void
{
    json_out(['status' => 'true', 'message' => 'success', $key => $rows]);
}

function fail(string $message, int $code = 200): void
{
    json_out(['status' => 'false', 'message' => $message], $code);
}

function cors(): void
{
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Headers: Content-Type, X-Requested-With');
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    if (($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'OPTIONS') {
        http_response_code(204);
        exit;
    }
}

/** Read request params from query string or JSON/form body. */
function params(): array
{
    static $p = null;
    if (is_array($p)) {
        return $p;
    }
    $p = $_GET + $_POST;
    $raw = file_get_contents('php://input');
    if ($raw !== '' && $raw !== false) {
        $decoded = json_decode($raw, true);
        if (is_array($decoded)) {
            $p = $decoded + $p;
        }
    }
    return $p;
}

function p_str(string $key, string $default = ''): string
{
    $v = params()[$key] ?? $default;
    return is_scalar($v) ? trim((string) $v) : $default;
}

function p_int(string $key, int $default = 0): int
{
    return (int) (params()[$key] ?? $default);
}

/** Shape a posts row into the exact JSON the Android client parses. */
function shape_post(array $r): array
{
    $item = [
        'jobTitle'        => (string) $r['job_title'],
        'jobContent'      => (string) $r['job_content'],
        'postid'          => (string) $r['postid'],
        'postPublishDate' => (string) $r['publish_date'],
        'formattedDate'   => (string) $r['formatted_date'],
    ];
    if (array_key_exists('expired_date', $r)) {
        $item['expiredDate'] = (string) ($r['expired_date'] ?? '');
    }
    return $item;
}
