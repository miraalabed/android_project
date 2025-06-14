<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "schoolsystem");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit();
}

$email = $_POST['email'] ?? '';
$pass = $_POST['pass'] ?? '';

if (empty($email) || empty($pass)) {
    echo json_encode(["status" => "error", "message" => "Missing email or password"]);
    exit();
}

$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "User not found"]);
    exit();
}

$user = $result->fetch_assoc();

if ($user['pass'] !== $pass) {
    echo json_encode(["status" => "error", "message" => "Incorrect password"]);
    exit();
}

// الرد حسب الدور
$response = [
    "status" => "success",
    "id" => $user['id'],
    "name" => $user['full_name'],
    "role" => $user['role'],
    "email" => $user['email']
];

if ($user['role'] === "Student") {
    $response["national_id"] = $user["national_id"];
    $response["class_grade"] = $user["class_grade"];
}

echo json_encode($response);
$conn->close();
?>