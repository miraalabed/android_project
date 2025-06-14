<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(['status' => 'error', 'message' => 'Connection failed']));
}

$search = isset($_GET['search']) ? $_GET['search'] : '';

$sql = "SELECT s.subject_name, s.description, u.full_name AS teacher_name
        FROM subjects s
        LEFT JOIN users u ON s.teacher_id = u.id
        WHERE s.subject_name LIKE ? OR s.description LIKE ?";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    die(json_encode(['status' => 'error', 'message' => 'Prepare failed: ' . $conn->error]));
}

$searchTerm = "%" . $search . "%";
$stmt->bind_param("ss", $searchTerm, $searchTerm);
$stmt->execute();

$result = $stmt->get_result();
$subjects = [];

while ($row = $result->fetch_assoc()) {
    $subjects[] = $row;
}

header('Content-Type: application/json');
echo json_encode($subjects);

$conn->close();
?>
