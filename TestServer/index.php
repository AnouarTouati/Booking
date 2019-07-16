<?php




$Message=$_POST["Message"];

$resultToApp["Message"]=$Message;
echo json_encode($resultToApp);
?>

