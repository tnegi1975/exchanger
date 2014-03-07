var bitcoinModule = angular.module("money.bitcoin");

bitcoinModule.controller("BitcoinController", function($scope, $rootScope, bitcoinResource) {
	$scope.amount = {
		coins: 0,
		cents: 0,
		centsInCoin: 100 * 1000 * 1000
	};

	$scope.fillInTransfer = function() {
		var orderInfo = $rootScope.orderInfo;
		orderInfo.inTransfer.amount = $scope.amount;
	};
	$scope.fillOutTransfer = function() {
		var orderInfo = $rootScope.orderInfo;
		orderInfo.outTransfer.address = $scope.address;
		orderInfo.outTransfer.amount = $scope.amount;
	};

	if ($rootScope.orderInfo.inTransfer.currency === "BITCOIN") {
		$scope.$watch("amount", $scope.fillInTransfer);
		var walletAddress = bitcoinResource.getNewAddress();
		walletAddress.$promise.then(function() {
			$scope.address = walletAddress.address;
			$rootScope.orderInfo.inTransfer.address = $scope.address;
		});
	} else {
		$scope.$watch("amount", $scope.fillOutTransfer);
		$scope.$watch("address", $scope.fillOutTransfer);
	}
});