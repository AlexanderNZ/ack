(function() {
    'use strict';
    angular
        .module('ackApp')
        .factory('Nomination', Nomination);

    Nomination.$inject = ['$resource', 'DateUtils'];

    function Nomination ($resource, DateUtils) {
        var resourceUrl =  'api/nominations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.nominationDate = DateUtils.convertLocalDateFromServer(data.nominationDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.nominationDate = DateUtils.convertLocalDateToServer(copy.nominationDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.nominationDate = DateUtils.convertLocalDateToServer(copy.nominationDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
