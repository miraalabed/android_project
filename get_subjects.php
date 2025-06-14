<?php
// get_subjects.php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");

$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode([
        'status' => 'error',
        'message' => 'Connection failed: ' . $conn->connect_error
    ]));
}

$teacher_id = isset($_GET['teacher_id']) ? intval($_GET['teacher_id']) : 0;

if ($teacher_id <= 0) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid or missing teacher_id'
    ]);
    exit;
}

$sql = "SELECT subject_id, subject_name FROM subjects WHERE teacher_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$subjects = [];
while ($row = $result->fetch_assoc()) {
    $subjects[] = $row;
}

echo json_encode($subjects);

$stmt->close();
$conn->close();
?>
