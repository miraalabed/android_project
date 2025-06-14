<?php
include("db_connection.php");

$sql = "SELECT * FROM users";  
$result = $conn->query($sql);

$users = array();

while ($row = $result->fetch_assoc()) {
    $users[] = $row;
}

echo json_encode($users);
$conn->close();
?>
