/**
 * Created by lena on 23.07.16.
 */


 'use strict';

 // Register `phoneList` component, along with its associated controller and template
 angular.
 module('matchList').
 component('matchList', {
 templateUrl: 'match-list/match-list.template.html',
 controller: ['Match',
 function MatchListController(Match) {
 this.matches = Match.query();
 //this.orderProp = 'age';
 }
 ]
 });
 
