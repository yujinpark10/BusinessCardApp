<?php
	$con = mysqli_connect("localhost", "yujinpark10", "qkr147147!!", "yujinpark10");
	mysqli_query($con,'SET NAMES utf8');
	
	$userID = $_POST["userID"];//Id 받아오기
	
	//내꺼 니꺼 sql변수 설정
	$mainlistme = "SELECT * FROM card WHERE userID = card.userID AND card.mine = 1";
	$mainlistyou = "SELECT * FROM card WHERE userID = card.userID AND card.mine = 0" ;

	$resultme = mysqli_query($con, $mainlistme);
	$resultyou =  mysqli_query($con, &mainlistyou);
	
	$responseme = array();
	$responseyou = array();


	while($rowme = mysqli_fetch_array($resultme)){
		array_push($responseme, "cardNum"=>$rowme[0], "name"=>$rowme[1], "company"=>$rowme[2], "team"=>$rowme[3], "position"=>$rowme[4], "coNum"=>$rowme[5], "num"=>$rowme[6], "e_mail"=>$rowme[7], "faxNum"=>$rowme[8], "address"=>$rowme[9], "userID"=>$rowme[10], "mine"=>$rowme[11]));
		}
	
	while($rowyou = mysqli_fetch_array($resultyou)){
		array_push($responseyou, "cardNum"=>$rowyou[0], "name"=>$rowyou[1], "company"=>$rowyou[2], "team"=>$rowyou[3], "position"=>$rowyou[4], "coNum"=>$rowyou[5], "num"=>$rowyou[6], "e_mail"=>$rowyou[7], "faxNum"=>$rowyou[8], "address"=>$rowyou[9], "userID"=>$rowyou[10], "mine"=>$rowyou[11]));
		}

	echo json_encode(array("responseyou"=>$responseyou));
	mysqli_close($con);
?>