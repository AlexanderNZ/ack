(function() {
    'use strict';

    angular
        .module('ackApp')
        .controller('NominationDetailController', NominationDetailController);

    NominationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Nomination', 'Person'];

    function NominationDetailController($scope, $rootScope, $stateParams, previousState, entity, Nomination, Person) {
        var vm = this;

        vm.nomination = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ackApp:nominationUpdate', function(event, result) {
            vm.nomination = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
