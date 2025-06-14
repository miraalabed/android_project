<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");

try {
    $pdo = new PDO('mysql:host=localhost;dbname=schoolsystem', 'root', '');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // نستخرج فقط الطلاب (role = 'Student')
    $stmt = $pdo->prepare("SELECT id, full_name FROM users WHERE role = 'Student'");
    $stmt->execute();
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data' => $students
    ]);

} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error',
        'error_details' => $e->getMessage()
    ]);
}
?>
