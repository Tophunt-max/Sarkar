<?php
/**
 * seed_demo.php — insert demo content if the database is empty.
 * Usage:  php seed_demo.php
 */
declare(strict_types=1);
require_once __DIR__ . '/config.php';

$pdo = db();

if ((int) $pdo->query('SELECT COUNT(*) FROM posts')->fetchColumn() > 0) {
    fwrite(STDOUT, "Posts already exist — nothing to seed.\n");
    exit(0);
}

$demo = [
    'govtjob' => [
        ['UPSC ESE (Preliminary) Exam 2024 — 247 Vacancies', 'Union Public Service Commission invites online applications for Engineering Services Examination 2024. Total 247 posts across Civil, Mechanical, Electrical and Electronics branches. <b>Eligibility:</b> B.E / B.Tech or equivalent. <b>Age:</b> 21–30 years. <b>Application fee:</b> ₹200 (exempted for SC/ST/PwD/Female).'],
        ['SSC CGL 2024 — 17,727 Posts Notification Out', 'Staff Selection Commission has released the Combined Graduate Level Examination 2024 notification for 17,727 vacancies in Group B and C posts. Graduation in any discipline required. Tier-1 exam will be held in September 2024.'],
        ['RRB NTPC 2024 — 11,558 Vacancies for Graduates', 'Railway Recruitment Board invites online applications for Non-Technical Popular Categories. Graduate and undergraduate posts available across all zones. Apply before 28 July 2024.'],
        ['IBPS PO XV Recruitment — 4,455 Vacancies', 'Institute of Banking Personnel Selection announces Probationary Officer recruitment for 2024–25. Any graduate may apply. Preliminary exam in October 2024.'],
    ],
    'currentaffairs' => [
        ['Daily Current Affairs — 09 November', 'National: The Union Cabinet approved the PM Vishwakarma scheme extension. International: India and UAE signed a bilateral investment treaty. Sports: India won the ICC T20 series 3-1. Awards: Dr. R. Chidambaram received the Lifetime Achievement Award in Science.'],
        ['Current Affairs Quiz — Important Days in November', 'November 14 is observed as World Diabetes Day and Children\'s Day in India. November 19 marks World Toilet Day. National Education Day is remembered on November 11 in honour of Maulana Abul Kalam Azad.'],
        ['Weekly Current Affairs Capsule — Economy', 'RBI kept the repo rate unchanged at 6.5%. India\'s GDP grew by 7.8% in Q1 FY25. Forex reserves reached USD 675 billion. Inflation eased to 4.3% in September.'],
    ],
    'resultpost' => [
        ['SSC CHSL 2024 Tier-I Result Declared', 'The Staff Selection Commission has declared the Tier-I result for Combined Higher Secondary Level Examination 2024. Candidates can check their marks and download the scorecard using their roll number and registration password.'],
        ['UPSC Civil Services Final Result 2024', 'The Union Public Service Commission has announced the final result of Civil Services Examination 2024. A total of 1,016 candidates have been recommended for appointment to IAS, IPS, IFS and other central services.'],
        ['IBPS Clerk Prelims Result 2024 Out', 'IBPS has released the preliminary examination result for Clerk recruitment 2024. Shortlisted candidates will appear for the Main examination in December.'],
    ],
    'dailynews' => [
        ['Today\'s Newspaper Headlines — 09 November', 'Top stories: New rail corridor approved in the North-East, ISRO announces Gaganyaan test flight window, RBI issues digital lending guidelines, and a record harvest forecast for the Rabi season.'],
        ['Employment News Weekly Highlights', 'This week in Employment News: 12,000+ vacancies in state PSUs, teaching faculty recruitment in central universities, and apprenticeship openings in Indian Railways.'],
    ],
];

$insert = $pdo->prepare(
    'INSERT INTO posts (postid, feed, job_title, job_content, publish_date, formatted_date, expired_date, is_expired)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?)'
);

$i = 0;
foreach ($demo as $feed => $items) {
    foreach ($items as $item) {
        $i++;
        $publish = date('Y-m-d', strtotime("-{$i} days"));
        $expired = date('d M Y', strtotime('-1 day'));
        $isExpired = ($feed === 'govtjob' && $i % 4 === 0) ? 1 : 0;
        $insert->execute([
            'SRK' . str_pad((string) $i, 5, '0', STR_PAD_LEFT),
            $feed,
            $item[0],
            $item[1],
            $publish,
            date('d M Y', strtotime($publish)),
            in_array($feed, ['govtjob'], true) ? $expired : '',
            $isExpired,
        ]);
    }
}

$filters = [
    ['Category', 'Banking & Insurance'],
    ['Category', 'Railway'],
    ['Category', 'Defence'],
    ['Category', 'Teaching'],
    ['Profession', 'Engineering'],
    ['Profession', 'Medical'],
    ['Profession', 'Law'],
    ['Qualification', '10th Pass'],
    ['Qualification', '12th Pass'],
    ['Qualification', 'Graduate'],
    ['Qualification', 'Post Graduate'],
    ['Location', 'Delhi'],
    ['Location', 'Maharashtra'],
    ['Location', 'Karnataka'],
    ['Location', 'All India'],
];
$fstmt = $pdo->prepare('INSERT INTO filters (group_name, category_name) VALUES (?, ?)');
$filterIds = [];
foreach ($filters as $f) {
    $fstmt->execute($f);
    $filterIds[$f[1]] = (int) $pdo->lastInsertId();
}

// Tag every demo govt-job post with a few labels so the "Find Job ... wise" lists have data.
$demoLabels = ['Graduate', 'All India', 'Engineering', 'Railway', 'Banking & Insurance'];
$link = $pdo->prepare('INSERT IGNORE INTO post_labels (post_id, filter_id) VALUES (?, ?)');
$jobIds = $pdo->query("SELECT id FROM posts WHERE feed = 'govtjob' ORDER BY id")->fetchAll(PDO::FETCH_COLUMN);
foreach ($jobIds as $n => $postId) {
    $link->execute([(int) $postId, $filterIds['Graduate']]);
    $link->execute([(int) $postId, $filterIds['All India']]);
    $link->execute([(int) $postId, $filterIds[$demoLabels[2 + ($n % 3)]]]);
}

$nstmt = $pdo->prepare('INSERT INTO notifications (main_title, second_title, publish_date) VALUES (?, ?, ?)');
$nstmt->execute(['SSC CGL 2024 notification released', 'Apply online before 24 July 2024', date('Y-m-d')]);
$nstmt->execute(['UPSC ESE admit card available', 'Download from the official website', date('Y-m-d', strtotime('-2 days'))]);
$nstmt->execute(['New railway vacancy announced', '11,558 posts across all zones', date('Y-m-d', strtotime('-5 days'))]);

fwrite(STDOUT, "Seeded {$i} posts, " . count($filters) . " filters, 3 notifications.\n");
