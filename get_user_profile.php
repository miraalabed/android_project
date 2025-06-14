<?php
header("Content-Type: application/json");

$host = "localhost";
$user = "root";
$password = "";
$database = "schoolsystem";

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $userId = $_GET['user_id'] ?? null;

    if (!$userId) {
        echo json_encode(["error" => "Missing user_id"]);
        exit;
    }

    $stmt = $conn->prepare("SELECT full_name, national_id, email, role, birth_date , address FROM users WHERE id = ?");
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($user = $result->fetch_assoc()) {
        echo json_encode($user);
    } else {
        echo json_encode(["error" => "User not found"]);
    }

    $stmt->close();
} else {
    echo json_encode(["error" => "Invalid request method"]);
}

$conn->close();
?>
