<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

try {
    // Connect to the database
    $pdo = new PDO('mysql:host=localhost;dbname=schoolsystem', 'root', '');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Receive data from the app
    $teacher_id = (int)($_POST['teacher_id'] ?? 0);
    $student_id = (int)($_POST['student_id'] ?? 0);
    $subject_id = (int)($_POST['subject_id'] ?? 0);
    $exam_name = trim($_POST['exam_name'] ?? '');
    $mark = trim($_POST['mark'] ?? '');

    // Validate inputs
    if (!$teacher_id || !$student_id || !$subject_id || $exam_name === '' || $mark === '') {
        echo json_encode([
            'success' => false,
            'message' => 'Required fields are missing or invalid',
            'received' => $_POST
        ]);
        exit;
    }

    if (!is_numeric($mark)) {
        echo json_encode([
            'success' => false,
            'message' => 'The mark must be a numeric value'
        ]);
        exit;
    }

    // Insert the mark into the database
    $stmt = $pdo->prepare("INSERT INTO marks (student_id, subject_id, exam_name, mark, date)
                           VALUES (?, ?, ?, ?, CURDATE())");
    $stmt->execute([$student_id, $subject_id, $exam_name, $mark]);

    echo json_encode([
        'success' => true,
        'message' => 'Mark has been successfully published'
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'A database error occurred',
        'error_details' => $e->getMessage()
    ]);
}
?>
