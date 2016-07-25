/**
 * Created by lena on 23.07.16.
 */


 'use strict';

 // Register `phoneDetail` component, along with its associated controller and template
 angular.
 module('matchDetail').
 component('matchDetail', {
 templateUrl: 'match-detail/match-detail.template.html',
 controller: ['$routeParams', 'Match',
 function MatchDetailController($routeParams, Match) {
 var self = this;
 self.match = Match.get({matchId: $routeParams.matchId}, function(match) {
 //self.setImage(phone.images[0]);
 });

 // self.setImage = function setImage(imageUrl) {
 // self.mainImageUrl = imageUrl;
 // };
 }
 ]
 });
 
