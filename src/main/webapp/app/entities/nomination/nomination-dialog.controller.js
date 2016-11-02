(function() {
    'use strict';

    angular
        .module('ackApp')
        .controller('NominationDialogController', NominationDialogController);

    NominationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Nomination', 'Person'];

    function NominationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Nomination, Person) {
        var vm = this;

        vm.nomination = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.nomination.nominationDate = new Date();
        vm.save = save;
        vm.people = Person.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.nomination.id !== null) {
                Nomination.update(vm.nomination, onSaveSuccess, onSaveError);
            } else {
                Nomination.save(vm.nomination, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ackApp:nominationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.nominationDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
