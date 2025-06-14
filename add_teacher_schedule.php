<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = "localhost";
$db = "schoolsystem"; 
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(['status' => 'error', 'message' => 'Connection failed: ' . $conn->connect_error]));
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // استقبال البيانات
    $teacher_id   = isset($_POST['teacher_id'])   ? intval($_POST['teacher_id'])   : 0;
    $subject_id   = isset($_POST['subject_id'])   ? intval($_POST['subject_id'])   : 0;
    $class_grade  = isset($_POST['class_grade'])  ? intval($_POST['class_grade'])  : 0;
    $day          = isset($_POST['day_of_week'])  ? trim($_POST['day_of_week'])    : '';
    $period       = isset($_POST['period_number'])? intval($_POST['period_number']): 0;
    $start        = isset($_POST['start_time'])   ? trim($_POST['start_time'])     : '';
    $end          = isset($_POST['end_time'])     ? trim($_POST['end_time'])       : '';

    // تحقق من البيانات المطلوبة
    if ($teacher_id <= 0 || $subject_id <= 0 || $class_grade <= 0 || empty($day) || $period <= 0 || empty($start) || empty($end)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing or invalid data']);
        exit;
    }

    // تحقق من تنسيق الوقت والمدة (50 دقيقة)
    $start_dt = DateTime::createFromFormat('H:i', $start);
    $end_dt = DateTime::createFromFormat('H:i', $end);
    if (!$start_dt || !$end_dt) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid time format (expected HH:mm)']);
        exit;
    }

    $interval = $start_dt->diff($end_dt);
    $total_minutes = ($interval->h * 60) + $interval->i;
    if ($total_minutes != 50) {
        echo json_encode(['status' => 'error', 'message' => 'Period duration must be exactly 50 minutes']);
        exit;
    }

    // تحقق من عدم وجود تعارض في الجدول
    $check_sql = "SELECT * FROM teacherschedule WHERE teacher_id = ? AND day_of_week = ? AND period_number = ?";
    $check_stmt = $conn->prepare($check_sql);
    if (!$check_stmt) {
        echo json_encode(['status' => 'error', 'message' => 'Prepare check failed: ' . $conn->error]);
        exit;
    }
    $check_stmt->bind_param("isi", $teacher_id, $day, $period);
    $check_stmt->execute();
    $conflictResult = $check_stmt->get_result();

    if ($conflictResult->num_rows > 0) {
        echo json_encode(['status' => 'error', 'message' => 'Conflict: Teacher already has a class in this period']);
        exit;
    }

    // إضافة الجدول
    $insert_sql = "INSERT INTO teacherschedule (teacher_id, subject_id, class_grade, day_of_week, period_number, start_time, end_time)
                   VALUES (?, ?, ?, ?, ?, ?, ?)";
    $insert_stmt = $conn->prepare($insert_sql);
    if (!$insert_stmt) {
        echo json_encode(['status' => 'error', 'message' => 'Prepare insert failed: ' . $conn->error]);
        exit;
    }
    $insert_stmt->bind_param("iiisiss", $teacher_id, $subject_id, $class_grade, $day, $period, $start, $end);

    if ($insert_stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Schedule added successfully']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Insert failed: ' . $insert_stmt->error]);
    }

    $check_stmt->close();
    $insert_stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
}
$conn->close();
?>