<?php
$host = "localhost";
$db = "schoolsystem"; 
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(['status' => 'error', 'message' => 'Connection failed: ' . $conn->connect_error]));
}

$search = isset($_GET['search']) ? $_GET['search'] : '';
$role = isset($_GET['role']) ? $_GET['role'] : 'All';

$params = ["%$search%"];

if ($role === "All") {
    $sql = "SELECT id, full_name as name , role FROM users WHERE full_name LIKE ? AND (role = 'Teacher' OR role = 'Student') ORDER BY RAND()";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $params[0]);
} else {
    $sql = "SELECT id, full_name as name, role FROM users WHERE full_name LIKE ? AND role = ? ORDER BY RAND()";
    $params[] = $role;
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $params[0], $params[1]);
}

$stmt->execute();
$result = $stmt->get_result();

$users = [];
while ($row = $result->fetch_assoc()) {
    $users[] = $row;
}

echo json_encode($users);
$conn->close();
?>
