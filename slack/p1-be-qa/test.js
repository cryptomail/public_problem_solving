var assert = require('assert');
var Promise = require('promise');
var request = require('request');
var fs = require('fs');

var secretToken = process.env.SLACK_SECRET_TOKEN;
var util = require('./util.js')(secretToken);

describe('Test suite to test Slack 3 API Endpoints: files.upload, files.list, files.delete', function() {

/*
Library setup and initialization:
*/

/*
Never store keys in source code!  Only in configuration where access can/should be limited!
*/
	
	if(!secretToken) {
		assert(1==2,'could not get initialize secret token not defined!');
	}
});