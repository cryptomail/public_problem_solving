
/*
Joshua Teitelbaum 4/12/2016
Testing the Slack FILE API
*/

/*
Main musical background for creation of this library:
https://www.youtube.com/watch?v=vSbQMxrt93o&nohtml5=False
*/

var assert = require('assert');
var Promise = require('promise');
var request = require('request');
var fs = require('fs');

/*
Secret key never stored in source code, and only is accessible through configuration files not in any RCS
*/
var secretToken = process.env.SLACK_SECRET_TOKEN;


/*
Switchable environment qa.slack.com/api, devint.slack.com/api, or default slack.com/api/
*/
var baseURL = process.env.SLACK_API_BASEURL;


/*
Global storage area of (responses) from files we uploaded
*/
var filesUploaded = {};

/*
If the base URL is not set assume we're testing PROD!
!YOLO! :)
*/
if(!baseURL) {
	baseURL = "https://slack.com/api/";
}

var util = require('./util.js')(baseURL, secretToken);

/*
Oh node, y u so silly and not include?
*/

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

describe('Test suite to test Slack 3 API Endpoints: files.upload, files.list, files.delete', function() {

/*
Library setup and initialization:
*/

/*
Never store keys in source code!  Only in configuration where access can/should be limited!
*/
	describe('Library initialization for testing the file suite', function() {

		it('Has tokens initialized', function(done) { 
			if(!secretToken) {
				assert(1==2,'could not get initialize secret token not defined!');
			}
			done();
		});

		it('Can load channels', function(done) {
			util.getChannels().then( function success(channels) {
				assert(channels);
				channels =
				done();
			},
			function error(e) {
				util.errorlog('Met error!',e);
				assert(1==2,'Could not resolve channels');
				done();
			});
		});
		it('Can load / find general channel', function(done) {
			util.getChannel('general').then( function success(channel) {
				assert(channel);
				done();
			},
			function error(e) {
				util.errorlog('Met error!',e);
				assert(1==2,'Could not resolve channel general');
				done();
			});
		});

	});

	describe('Begin testing the files api!', function() {
		this.timeout(20000);
		it('Positive: Can upload a file', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token: secretToken,
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
						
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
			
					assert(data.id != null,'data id defined');
					/*
					Record what we wrote for later cleanup
					*/
					filesUploaded[data.id] = data;

					/*
					TODO: this is a little -funroll-loops here :\
					*/
					assert(data.thumb_64 != null,'thumb_64 defined');
					assert(data.thumb_80 != null,'thumb_80 defined');
					assert(data.thumb_360 != null,'thumb_360 defined');
					assert(data.thumb_160 != null,'thumb_160 defined');
					assert(data.channels && data.channels.length == 0);

					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));
					done();
				    
		  		},
		  	function error(e) {
		  		assert(1==2,"Returned something other than 500 :)");
		  		done();
		  	});

		});
		it('Positive: Can upload a file with unknown field: muppets!', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token: secretToken,
							muppets: 'animal, kermit, miss piggy',
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
			
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
			
					assert(data.id != null,'data id defined');
					/*
					Record what we wrote for later cleanup
					*/
					filesUploaded[data.id] = data;

					/*
					TODO: this is a little -funroll-loops here :\
					*/
					assert(data.thumb_64 != null,'thumb_64 defined');
					assert(data.thumb_80 != null,'thumb_80 defined');
					assert(data.thumb_360 != null,'thumb_360 defined');
					assert(data.thumb_160 != null,'thumb_160 defined');
					assert(data.channels && data.channels.length == 0);

					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));
					done();
				    
		  		},
		  	function error(e) {
		  		assert(1==2,"Returned something other than 500 :)");
		  		done();
		  	});
			
		});
		it('Positive: Unknown explicit typefile', function(done) {
			
			var fnameBase = 'really_a_pdf';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							filetype: 'kermitthefrog',
							token:  secretToken ,
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
						
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
					console.log(data);
					filesUploaded[data.id] = data;
					//Server performed correct magic on file!
					//Would be interesting to know what binary library is being used because of security risks there :) ... or rather >:]
					assert(data.filetype && data.filetype.toLowerCase() === 'pdf')
					done();
				    
		  		},
		  	function error(e) {
		  		util.errorlog(e);
		  		assert(false,'got error ' + e);
		  		done();
		  	});

		});
		it('Negative: Invalid Auth', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token: 'MissPiggy' + secretToken + 'Kermit',
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
					
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
					console.log(data);
					assert(1==2,'How did this happen?  Why can we upload a file with invalid auth?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		assert(!e.ok && e.error === util.FilesErrorStates.INVALID_AUTH);
		  		done();
		  	});

		});
		it('Negative: No Auth Provided', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
						
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
					
					assert(1==2,'How did this happen?  Why can we upload a file with invalid auth?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		
		  		assert(!e.ok && e.error === util.FilesErrorStates.NOT_AUTHED);
		  		done();
		  	});

		});
		it('Negative: Empty file', function(done) {
			
			var fnameBase = 'bunk';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token:  secretToken ,
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
						
			util.issueSimplePOSTRequest('files','upload',formData).then(
				function success(data) {
					console.log(data);
					assert(1==2,'How did this happen?  Why can we upload a file with zero content auth?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		assert(!e.ok && e.error === util.FilesErrorStates.NO_FILE);
		  		done();
		  	});

		});

	});

});