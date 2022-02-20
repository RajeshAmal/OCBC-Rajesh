<%@page import="com.ocbc.controller.LoginController"%>
<%@page import="java.text.*,java.util.*" %>
<%@page import="javax.servlet.*" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="ISO-8859-1">
<title>OCBC</title>

<script type="text/javascript">

function login()
{
	var username = document.getElementById("loginValue").value;
	var payTo = document.getElementById("payToVaue").value;
	var amount = document.getElementById("transAmount").value;
	
	if(username="")
	{
		alert("Please enter Login Person Username");
	}else{
		$.ajax(
			{
				url:"<%= request.getContextPath()%>/LoginController?type=loginPersonBalanace",
				data:'username='+username,
				type:"GET",
				success:function(data)
				{
					dd=JSON.stringify(result);
					d=JSON.parse(dd);
					document.getElementById("balance").value=d;
				}
			});
	}
	if(payTo="")
	{
		alert("Please enter transfer person Name");
	}
	if(amount="")
	{
		alert("Please enter the Amount");
	}
	balance = document.getElementById("balance").value;
	
	if(confirmn('Are you sure to transfer the amount to ' + payTo)){
		$.ajax(
				{
					url:"<%= request.getContextPath()%>/LoginController?type=transfer",
					data:'username='+username+'&payTo='+payTo+'&amount='+amount+'&balance='+balance,
					type:"POST",
					success:function(data)
					{
						if(data=="Failed"){
							alert("Not Valid login user or transfer person user.. !!");
						}
					}
				});
	}
	
}

</script>
</head>
<body>
	<div id="page-container">
		<div id="content">
			<div id="login">
				Amount Transaction
			</div>
			<div>
				<form role="form" action="<%= request.getContextPath()%>/LoginController">
					<div>
						<div>
							Login : 
							<input type="text" id="loginValue" placeholder="Login">
						</div>
					</div>
					
					<div>
						<div>
							Balance Amount : 
							<input type="text" id="balance" placeholder="Balance" readonly>
						</div>
					</div>
				
					<div>
						<div>
							Pay To : 
							<input type="text" id="payToValue" placeholder="PayTo" >
							<input type="text" id="transAmount" placeholder="Amount" >
						</div>
					</div>
					
					<div>
						<div>
							<input type="button" name="submit" value="Transfer" onclick="Login();" />
						</div>
					</div>
				
				</form>
			</div>
		</div>
	</div>
</body>
</html>