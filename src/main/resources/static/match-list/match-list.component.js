'use strict';

// Register `phoneList` component, along with its associated controller and template
angular.module('matchList').component('matchList', {
    templateUrl: 'match-list/match-list.template.html',
    controller: ['Match', function MatchListController(Match) {
        var date = new Date();
        date.setHours(0,0,0,0);
        this.firstCategory = Match.query({category: 1});

        this.secondCategory = Match.query({category: 2});

        this.startParsing = function() {
            $http({
            method: 'GET',
            url: '/match'
        }).then(function successCallback(response) {

            }, function errorCallback(response) {

            })};

    }]
});

/*
 $http({
 method: 'GET',
 url: '/someUrl'
 }).then(function successCallback(response) {
 // this callback will be called asynchronously
 // when the response is available
 }, function errorCallback(response) {
 // called asynchronously if an error occurs
 // or server returns response with an error status.
 });
 */
 
