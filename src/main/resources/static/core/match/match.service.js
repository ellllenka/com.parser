'use strict';

angular.module('core.match').factory('Match', ['$resource',
    function ($resource) {
        return $resource('/matches/:category', {}, {
            query: {
                method: 'GET',
                params: {
                    category: 'category'
                },
                isArray: true
            }
        });
    }
]);
