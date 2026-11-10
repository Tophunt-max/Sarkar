<?php
/** GET api/getnotification.php → { status, message, notification: [{maintitle, secondtitle, publishdate}] } */
declare(strict_types=1);
require_once dirname(__DIR__) . '/config.php';

cors();

try {
    $stmt = db()->query(
        'SELECT main_title, second_title, publish_date FROM notifications
          ORDER BY publish_date DESC, id DESC LIMIT 200'
    );
    $rows = [];
    foreach ($stmt->fetchAll() as $r) {
        $rows[] = [
            'maintitle'   => (string) $r['main_title'],
            'secondtitle' => (string) $r['second_title'],
            'publishdate' => (string) $r['publish_date'],
        ];
    }
    ok('notification', $rows);
} catch (Throwable $e) {
    fail('Server error: ' . $e->getMessage());
}
