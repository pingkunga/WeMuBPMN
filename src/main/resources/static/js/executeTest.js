$( document).ready(function() {
	var IsPrevTestResultBinding = false;
	var IsBinding = false;

	$(window).bind("load", function() {
		// code here
		console.log("Initial Execute Test Result");
		var dataGrid = null;
		IsPrevTestResultBinding = true;
		bindListTestResultToGrid(dataGrid);

		IsBinding = true;
		bindTestResultToGrid(dataGrid);
	});

	$('#BPMNMutantModel').change(function() {
		var selected =  $("#BPMNMutantModel").children("option:selected").val();
		console.log(selected);

		if (selected === '') {
			console.log("Do Nothing");
			var dataGrid = null;
			bindListTestResultToGrid(dataGrid);
		}
		else{
			//แสดงรายงาน testResult ถ้ามี
			listTestResult(selected);
		}
	});

	$("#excelExport").click(function() {
		event.preventDefault();
		console.log("Export");
		//$("#mutantTreeGrid").jqxTreeGrid('exportData', 'xls');
		$("#mutantResultGrid").jqxGrid('exportdata', 'xls', 'jqxGrid');
	});
	// SUBMIT FORM
    $("#excuteTest").click(function(event) {
		// Prevent the form from submitting via the browser.
		event.preventDefault();

		//https://stackoverflow.com/questions/5316697/jquery-return-data-after-ajax-call-success
		uploadTestCase();
	});

	$("#generateTestReport").click(function(event) {
		// Prevent the form from submitting via the browser.
		event.preventDefault();
		
		mutantTestItemId = $("#BPMNMutantModel").children("option:selected").val();
		console.log(mutantTestItemId);

		window.open('/testExecution/generateTestResultReport?id='+mutantTestItemId);

		// var mututantTestItemId = $("#BPMNMutantModel").children("option:selected").val();
		// $.get('/testExecution/generateTestResultReport', 'id=' + mututantTestItemId, function(data) {
		// 	console.log("Generate Success");
		// });
	});

	function listTestResult(selected){
		var formData = {
    		mutantTestItemId : selected
		}

		console.log(formData);

		$.ajax({
			type : "POST",
			contentType : "application/json",
			url: "testExecution/listTestResultByMutantId",
			data : JSON.stringify(formData),
			dataType : 'json',
			success: function (data) {
	
				//$("#result").text(data);
				console.log("SUCCESS : ", data);
				//$("#btnSubmit").prop("disabled", false);
				//executeTest();
				bindListTestResultToGrid(data);
			},
			error: function (e) {
	
				//$("#result").text(e.responseText);
				console.log("ERROR : ", e);
				//$("#btnSubmit").prop("disabled", false);
	
			}
		});
	}

	function bindListTestResultToGrid(dataGrid){
		
		//alert(formData);
		console.log(dataGrid);

		//Call Get Data Source
		// testResultHeadId
		// testStart
		// testFinish
		// executionTime
		var source = {
			datatype: 'json',
			datafields: [{
				name: 'testResultHeadId',
				type: 'string'
			}
			, {
				name: 'testStart',
				type: 'string'
			}
			, {
				name: 'testFinish',
				type: 'string'
			}
			, {
				name: 'executionTime',
				type: 'int'
			}
			],
			//id: 'genFileName',
			localdata: dataGrid
		};

		//IsPrevTestResultBinding = true;
		//Create Data Source
		var dataAdapter = new $.jqx.dataAdapter(source);
		//Check ว่ามีข้อมูลใหม่ใน Grid หรือป่าว
		if (IsPrevTestResultBinding === true) {
			// jqGrid exist
			console.log("reload grid previous test " + IsPrevTestResultBinding);
			// passing "cells" to the 'updatebounddata' method will refresh only the cells values when the new rows count is equal to the previous rows count.
			//$("#mutantTreeGrid").jqxTreeGrid('updatebounddata', 'cells');
			//$("#mutantTreeGrid").trigger("reloadGrid");
			$("#testResultGrid").jqxGrid('destroy');
			$('#testResultGridContainer').after('<div id = "testResultGrid" ></div>');
			IsPrevTestResultBinding = false;
		}

		IsPrevTestResultBinding = true;
		console.log("new grid previous test " + IsBinding);

		$('#testResultGrid').jqxGrid({
			width: 380,
			autoheight: true,
			autorowheight: true,
			columnsresize: true,
            source: dataAdapter,
            columns: [{
                text: 'Id',
                datafield: 'testResultHeadId',
                width: 60
            }, {
                text: 'Start',
                datafield: 'testStart',
                width: 100
			}
			, {
                text: 'Finish',
                datafield: 'testFinish',
                width: 100
			}
			, {
                text: 'Time',
                datafield: 'executionTime',
                width: 60
			}
			,{
                text: 'Delete',
				datafield: 'button',
				columntype: 'button',
                cellsrenderer: function () {
						return "Delete";
					 }
					, buttonclick: function (row, event) {
						event.preventDefault();
						editrow = row;
						var dataRecord = $("#testResultGrid").jqxGrid('getrowdata', editrow);
						testResultHeadId = dataRecord.testResultHeadId;
						//alert(testResultHeadId);
						console.log(testResultHeadId);
						
						$.ajax({
							type: "DELETE",
							url: 'testExecution/testResult/delete?id=' + testResultHeadId,
							success: function (data) {
								console.log(data);
								var selected =  $("#BPMNMutantModel").children("option:selected").val();
								listTestResult(selected);
							},
							error: function (data) {
								console.log('Error:', data);
							}
						});
					}
			},
			]
        });
	}


	

    
	function uploadTestCase(){
		// PREPARE FORM DATA
		var formData = new FormData();
		formData.append('file', $('#uploadTestCaseFile')[0].files[0]);
		formData.append('mutantTestItemId', $("#BPMNMutantModel").children("option:selected").val());

		console.log(formData);

	
		$.ajax({
			type: "POST",
			//enctype: 'multipart/form-data',
			url: "testExecution/uploadTestCase",
			data: formData,
			//http://api.jquery.com/jQuery.ajax/
			//https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
			processData: false, //prevent jQuery from automatically transforming the data into a query string
			contentType: false,
			cache: false,
			timeout: 0, //3000 = sets timeout to 3 seconds Otherwise 0 No Timeout
			success: function (data) {
	
				//$("#result").text(data);
				console.log("SUCCESS : ", data);
				//$("#btnSubmit").prop("disabled", false);
				executeTest();
			},
			error: function (e) {
	
				//$("#result").text(e.responseText);
				console.log("ERROR : ", e);
				//$("#btnSubmit").prop("disabled", false);
			}
		});
	}

    function executeTest(){
    	
    	// PREPARE FORM DATA
    	var formData = {
    		mutantTestItemId : $("#BPMNMutantModel").children("option:selected").val()
		}
		//alert(formData);
		console.log(formData);

		//data : JSON.stringify(formData),
		console.log(JSON.stringify(formData));

		waitingDialog.show('Testing Mutants: Please wait...');
		// DO POST
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "testExecution/execute",
			data : JSON.stringify(formData),
			dataType : 'json',
			timeout: 6000000,
			success : function(result) {
				console.log(result);
				bindTestResultToGrid(result);
				waitingDialog.hide();
			},
			error : function(e) {
				//tmpResult = "error";
				setTimeout(function () {
					waitingDialog.hide();
				}, 1000);
				//alert("Error!")
				console.log("ERROR: ", e);
				console.log("ERROR: ", e.responseText);
				Swal.fire({
					type: 'error',
					title: 'error...',
					text: e.responseText,
				  })
			}
		});
    }
    
	function bindTestResultToGrid(dataGrid){
		
		//alert(formData);
		console.log(dataGrid);

		//Call Get Data Source
		var source = {
			datatype: 'json',
			datafields: [{
				name: 'mutantName',
				type: 'string'
			}
			, {
				name: 'testCaseName',
				type: 'string'
			}
			, {
				name: 'testCaseInput',
				type: 'string'
			}
			, {
				name: 'original',
				type: 'string'
			}
			, {
				name: 'mutant',
				type: 'string'
			}
			, {
				name: 'result',
				type: 'string'
			}
			, {
				name: 'remark',
				type: 'string'
			}
			, {
				name: 'executionTime',
				type: 'int'
			}
			//ค่าที่มีการ Replace ข้อมูลแล้ว
			, {
				name: 'evaloriginalstatement',
				type: 'string'
			}
			, {
				name: 'evalmutantstatement',
				type: 'string'
			}
			],
			//id: 'genFileName',
			localdata: dataGrid
		};

		//Create Data Source
		var dataAdapter = new $.jqx.dataAdapter(source);
		//Check ว่ามีข้อมูลใหม่ใน Grid หรือป่าว
		if (IsBinding === true) {
			// jqGrid exist
			console.log("reload grid" + IsBinding);
			// passing "cells" to the 'updatebounddata' method will refresh only the cells values when the new rows count is equal to the previous rows count.
			//$("#mutantTreeGrid").jqxTreeGrid('updatebounddata', 'cells');
			//$("#mutantTreeGrid").trigger("reloadGrid");
			$("#mutantResultGrid").jqxGrid('destroy');
			$('#mutantResultGridContainer').after('<div id = "mutantResultGrid" class="row justify-content-center"></div>');
			IsBinding = false;
		}

		IsBinding = true;
		console.log("new grid" + IsBinding);
		

		//Create Grid
		//var dataAdapter = new $.jqx.dataAdapter(source);

		$('#mutantResultGrid').jqxGrid({
			width: 1550,
			autoheight: true,
			autorowheight: true,
			columnsresize: true,
            source: dataAdapter,
            columns: [{
                text: 'Mutant Name',
                datafield: 'mutantName',
                width: 250
            }, {
                text: 'test Case',
                datafield: 'testCaseName',
                width: 100
			}
			, {
                text: 'Original Statement',
                datafield: 'original',
                width: 280
			}
			, {
                text: 'Mutant Statement',
                datafield: 'mutant',
                width: 280
			}
			, {
                text: 'Replace Original',
                datafield: 'evalOriginal',
				width: 280,
				hidden: true 
			}
			, {
                text: 'Replace Mutant',
                datafield: 'evalMutant',
				width: 280,
				hidden: true 
			}
			, {
                text: 'Test Result',
                datafield: 'result',
                width: 100
			}
			// , {
            //     text: 'Exec. Time',
            //     datafield: 'executionTime',
            //     width: 60
			// }

			, {
                text: 'Remark',
                datafield: 'remark',
                width: 500
			}]
            });
		
		// trigger the column resized event.
		$("#mutantResultGrid").on('columnresized', function (event) {
			var column = event.args.columntext;
			var newwidth = event.args.newwidth;
			var oldwidth = event.args.oldwidth;
			console.log("Column: " + column + ", " + "New Width: " + newwidth + ", Old Width: " + oldwidth);
			// $("#eventlog").text("Column: " + column + ", " + "New Width: " + newwidth + ", Old Width: " + oldwidth);
		});

		/*
		var listSource = [
			{ label: 'Replace Original', value: 'evaloriginalstatement', checked: false }
		  , { label: 'Replace Mutant', value: 'evalmutantstatement', checked: false }
		];
		$("#showReplaceMutantColumn").jqxListBox({ source: listSource, width: 200, height: 55,  checkboxes: true });
		$("#showReplaceMutantColumn").on('checkChange', function (event) {
			$("#mutantResultGrid").jqxGrid('beginupdate');
			if (event.args.checked) {
				alert("555");
				$("#mutantResultGrid").jqxGrid('showcolumn', event.args.value);
			}
			else {
				$("#mutantResultGrid").jqxGrid('hidecolumn', event.args.value);
			}
			$("#mutantResultGrid").jqxGrid('endupdate');
		});
		*/
			

	}
})