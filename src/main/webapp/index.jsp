<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html ng-app="main">
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<%@ include file="WEB-INF/jspf/head.jspf" %>
	</head>
	<body>
		<nav class="navbar navbar-default" role="navigation" ng-controller="EnvController">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" ng-click="isNavbarCollapsed = !isNavbarCollapsed">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">
						Bitgates <span class="label label-danger ng-hide" ng-show="env.demoEnabled">demo</span>
					</a>
				</div>
				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" collapse="isNavbarCollapsed">
					<%@ include file="WEB-INF/jspf/user.jspf" %>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
		<div class="container">
			<div class="jumbotron">
				<h1>Use this document as a way to quickly start any new project.</h1>
			</div>
		</div>
	</body>
</html>
