/**
 * Created by lena on 23.07.16.
 */

 'use strict';

 angular.
 module('parserApp').
 config(['$locationProvider' ,'$routeProvider',
 function config($locationProvider, $routeProvider) {
 $locationProvider.hashPrefix('!');

 $routeProvider.
 when('/matches', {
 template: '<match-list></match-list>'
 }).
 when('/matches/:matchId', {
 template: '<match-detail></match-detail>'
 }).
 otherwise('/matches');
 }
 ]);
 
