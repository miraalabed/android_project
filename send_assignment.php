<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

try {
    $pdo = new PDO('mysql:host=localhost;dbname=schoolsystem', 'root', '');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // استلام البيانات
    $teacher_id = (int)($_POST['teacher_id'] ?? 0);
    $subject_id = (int)($_POST['subject_id'] ?? 0);
    $class_grade = (int)($_POST['class_grade'] ?? 0);
    $title = trim($_POST['title'] ?? '');
    $description = trim($_POST['description'] ?? '');
    $due_date = trim($_POST['due_date'] ?? '');

    // التحقق من القيم المطلوبة
    if (!$teacher_id || !$subject_id || !$class_grade || $title === '' || $due_date === '') {
        echo json_encode([
            'success' => false,
            'message' => 'Missing or invalid fields',
            'received' => $_POST
        ]);
        exit;
    }

    // التحقق من تنسيق التاريخ
    if (!preg_match("/^\d{4}-\d{2}-\d{2}$/", $due_date)) {
        echo json_encode([
            'success' => false,
            'message' => 'Invalid date format. Use YYYY-MM-DD'
        ]);
        exit;
    }

    // إدخال البيانات في جدول assignments
    $stmt = $pdo->prepare("INSERT INTO assignments (teacher_id, subject_id, class_grade, title, description, due_date)
                           VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$teacher_id, $subject_id, $class_grade, $title, $description, $due_date]);

    echo json_encode([
        'success' => true,
        'message' => 'Assignment successfully created',
        'assignment_id' => $pdo->lastInsertId()
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Database error',
        'error_details' => $e->getMessage()
    ]);
}
?>
