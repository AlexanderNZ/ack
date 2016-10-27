(function() {
    'use strict';

    angular
        .module('ackApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('nomination', {
                parent: 'entity',
                url: '/nomination',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Nominations'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/nomination/nominations.html',
                        controller: 'NominationController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                }
            })
            .state('nomination-detail', {
                parent: 'entity',
                url: '/nomination/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Nomination'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/nomination/nomination-detail.html',
                        controller: 'NominationDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Nomination', function($stateParams, Nomination) {
                        return Nomination.get({id : $stateParams.id}).$promise;
                    }],
                    previousState: ["$state", function ($state) {
                        var currentStateData = {
                            name: $state.current.name || 'nomination',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                        return currentStateData;
                    }]
                }
            })
            .state('nomination-detail.edit', {
                parent: 'nomination-detail',
                url: '/detail/edit',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/nomination/nomination-dialog.html',
                        controller: 'NominationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Nomination', function(Nomination) {
                                return Nomination.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('^', {}, { reload: false });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('nomination.new', {
                parent: 'nomination',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/nomination/nomination-dialog.html',
                        controller: 'NominationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    value: null,
                                    reason: null,
                                    date: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function() {
                        $state.go('nomination', null, { reload: 'nomination' });
                    }, function() {
                        $state.go('nomination');
                    });
                }]
            })
            .state('nomination.edit', {
                parent: 'nomination',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/nomination/nomination-dialog.html',
                        controller: 'NominationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Nomination', function(Nomination) {
                                return Nomination.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('nomination', null, { reload: 'nomination' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('nomination.delete', {
                parent: 'nomination',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/nomination/nomination-delete-dialog.html',
                        controller: 'NominationDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Nomination', function(Nomination) {
                                return Nomination.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('nomination', null, { reload: 'nomination' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }

})();
