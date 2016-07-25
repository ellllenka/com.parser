/**
 * Created by lena on 23.07.16.
 */


 'use strict';

 angular.
    module('core.match').
    factory('Match', ['$resource',
    function($resource) {
    return $resource('matches/:matchId.json', {}, {
        // ??????????
    query: {
    method: 'GET',
    params: {matchId: 'matches'},
    isArray: true
    }
    });
    }
    ]);
