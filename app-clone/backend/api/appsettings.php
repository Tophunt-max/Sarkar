<?php
/** GET api/appsettings.php → { status, message, appsettings: [{admob_banner, ...}] } */
declare(strict_types=1);
require_once dirname(__DIR__) . '/config.php';

cors();

try {
    $rows = db()->query('SELECT setting_key, setting_value FROM app_settings')->fetchAll();
    $map  = [];
    foreach ($rows as $r) {
        $map[$r['setting_key']] = $r['setting_value'];
    }
    $setting = [
        'admob_banner'       => $map['admob_banner']       ?? '',
        'admob_interstitial' => $map['admob_interstitial'] ?? '',
        'admob_native'       => $map['admob_native']       ?? '',
        'admob_appopen'      => $map['admob_appopen']      ?? '',
    ];
    ok('appsettings', [$setting]);
} catch (Throwable $e) {
    fail('Server error: ' . $e->getMessage());
}
