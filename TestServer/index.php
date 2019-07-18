<?php



/*
$Message=$_POST["Message"];

$resultToApp["Message"]=$Message;
echo json_encode($resultToApp);*/
$_POST = json_decode(file_get_contents('php://input'), true);
$Image=$_POST["Image"];

$ResultToApp["Image"]=$Image;
echo json_encode($ResultToApp);

?>

