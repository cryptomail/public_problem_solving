
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
		this.timeout(40000);
		it('Initializes for idempotency', function(done) {
			done();
		},
		function error(e) {

		});
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
			
					/*
					This suffices the ***simple requirements** outlined in the exercise:
						files.upload
						Upload a PNG file no greater than 1 MB in size
						files.upload returns a file object with a file ID and all expected thumbnail URLs
						The thumbnail URLs point to what appear to be the correct file – the filename will be a lowercase version of the original upload

						Life is more complicated than happy paths....sigh :) ...go below till the end of this describe for files.upload
					*/
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

		it('Positive: unset filetype or LIED to DOT FILE resolves to correct filetype', function(done) {
			
			var fnameBase = 'really_a_pdf';
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
		it('Positive: Can upload a file to #general', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token: secretToken,
							channels: "#general",
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
					
					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));


					assert(data.channels && data.channels.length === 1);

					util.getChannel('general').then(function success(channeldata) {

						assert(channeldata.id == data.channels[0]);
						done();
					},
					function error(e) {
						util.errorlog(e);
						assert(false);
						done();
					});
					
				    
		  		},
		  	function error(e) {
		  		assert(1==2,"Returned something other than 500 :)");
		  		done();
		  	});

		});
		it('Positive: Can upload a file to #general twice', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							token: secretToken,
							channels: "#general,#general",
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
					
					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));

					assert(data.channels && data.channels.length === 1);

					util.getChannel('general').then(function success(channeldata) {

						assert(channeldata.id == data.channels[0]);
						done();
					},
					function error(e) {
						util.errorlog(e);
						assert(false);
						done();
					});
				    
		  		},
		  	function error(e) {
		  		util.errorlog(e);
		  		assert(1==2,"Returned something other than 500 :)");
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
					
					assert(1==2,'How did this happen?  Why can we upload a file with zero content auth?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		assert(!e.ok && e.error === util.FilesErrorStates.NO_FILE);
		  		done();
		  	});

		});
		it('Negative: Send to invalid channel', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							channels: 'dr_bunson_honeydew',
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
					
					assert(1==2,'How did this happen?  Why can we upload a file to a channel that doesnt exist?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		
		  		assert(!e.ok && e.error === util.FilesErrorStates.INVALID_CHANNEL);
		  		done();
		  	});

		});
		
		it('Evil: Send invalid blank channels', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							channels: util.blabber(',',100000),
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
					
					assert(1==2,'How did this happen?  Why can we upload a file to a channel that doesnt exist?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		
		  		assert(!e.ok && e.error === util.FilesErrorStates.INVALID_CHANNEL);
		  		done();
		  	});

		});
		it('Evil: Send a big gulp blank channel', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							channels: util.blabber(' ',1000000),
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
					
					assert(1==2,'How did this happen?  Why can we upload a file to a channel that doesnt exist?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		
		  		assert(!e.ok && e.error === util.FilesErrorStates.INVALID_CHANNEL);
		  		done();
		  	});

		});
		it('Evil: Send a big gulp file name, and get s3 error.\007 I would not do this.', function(done) {
			
			var fnameBase = util.blabber('kinderegg',1000) + '.png';
			var fname = 'kinderegg' + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							filename: fnameBase,
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
					
					assert(1==2,'How did this happen?  Why can we upload a file to a channel that doesnt exist?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		assert(!e.ok && e.error === 's3_failure');
		  		done();
		  	});

		});

		it('Evil: Send a big gulp title', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							filename: fnameBase,
							title: util.blabber('shmoo',100000),
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
					
					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));
					
					assert(data.title && data.title.length == util.Limits.FILE_TITLE_LENGTH);
					done();
				    
		  		},
		  	function error(e) {
		  		util.errorlog(e);

		  		done();
		  	});

		});

		it('Evil: Send a LOT OF invalid  channels', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							channels: util.blabber('a',100000,','),
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
					
					assert(1==2,'How did this happen?  Why can we upload a file to a channel that doesnt exist?');
					
					done();
				    
		  		},
		  	function error(e) {
		  		
		  		assert(!e.ok && e.error === util.FilesErrorStates.INVALID_CHANNEL);
		  		done();
		  	});

		});
		it('Evil: Send to A LOT OF valid channels\nThis should pass in my opinion.\007  I think the code is doing something less than optimal, or something REALLY smart!', function(done) {
			
			var fnameBase = 'kinderegg';
			var fname = fnameBase + '.png';
			var fnamePath = 'data' + '/' + fname;
			var formData = {
							channels: util.blabber('#general',1000,','),
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
					
					var fname64 = util.constructThumbFileNamePostfix(fnameBase,'_',64,'png');
					assert(data.thumb_64.endsWith(fname64));
					var fname80 = util.constructThumbFileNamePostfix(fnameBase,'_',80,'png');
					assert(data.thumb_80.endsWith(fname80));
					var fname360 = util.constructThumbFileNamePostfix(fnameBase,'_',360,'png');
					assert(data.thumb_360.endsWith(fname360));
					var fname160 = util.constructThumbFileNamePostfix(fnameBase,'_',160,'png');
					assert(data.thumb_160.endsWith(fname160));

					assert(data.channels && data.channels.length === 1);

					util.getChannel('general').then(function success(channeldata) {

						assert(channeldata.id == data.channels[0]);
						done();
					},
					function error(e) {
						util.errorlog(e);
						
						done();
					});
				    
		  		},
		  	function error(e) {
		  		
		  		assert(1==2,'Evil: Send to A LOT OF valid channels: This should pass in my opinion.  I think the code is doing something less than optimal!');
		  		done();
		  	});


		});

		it('Positive: Gets total file count', function(done) {
			util.getFileCount(null,null,null).then(function success(cnt) {
				assert(cnt >= 0);
				done();
			},
			function error(e) {
				util.errorlog(e);
				assert(1==2,'Get file count failed');
				done();
			})
		});
		it('Positive: Gets all files and matches count', function(done) {
			util.getAllFiles(null,null,null).then(function success(files) {
				assert(files != null);
				util.getFileCount(null,null,null).then( function(cnt) {
					assert(files.length === cnt,'We got all the files congrent to the count');
					done();
				},
				function error(e) {
					util.errorlog(e);
					assert(1==2,'Error on getting all files');
					done();
				})
				done();
			},
			function error(e) {
				util.errorlog(e);
				assert(1==2,'Get file count failed');
				done();
			})
		});

	});

});