function sumArray(arr) {
	var s = 0;

	arr.forEach( function(val,i,thearray ) {
		s += val;
	});

	return s;
}
function longestSpan(arr1, arr2) {
	if(arr1.length !== arr2.length) {
		throw new Exception('What are those?');
	}

	if(arr1.length <= 0) {
		throw new Exception('What are those');
	}
	var i;
	var localmax = 0;
	var blocks = {};
	var sum1 = -1,sum2 = -1;

	for(i=0; i < arr1.length; i++) {
		
		if(i === 0) {
			if(arr1[i] === arr2[i]) {
				localmax = 1;
			}

			blocks[0] = {b1:[[arr1[0]]], b2:[[arr2[0]]]};
		}
		else {

			var b1 = [];
			var b2 = [];
			var obj = {};
			var s = JSON.stringify(blocks[i-1]);
			obj = JSON.parse(s);
			
			
			b1.push(arr1[i]);
			b2.push(arr2[i]);
			obj.b1.unshift(b1);
			obj.b2.unshift(b2);
			var t = obj.b1.length-1;
			while(t > 0) {
				obj.b1[t].push(arr1[i]);
				obj.b2[t].push(arr2[i]);
				t--;
			}
			
			blocks[i] = obj;

			
			var x;
			for(x=0; x < obj.b1.length;x++) {
				var sa = sumArray(obj.b1[x]);
				var sb = sumArray(obj.b2[x]);
				if( sa === sb) {
					if(obj.b1[x].length >= localmax) {
						localmax = obj.b1[x].length;
					}
				}
			}


		}
	}
	
	return localmax;
}