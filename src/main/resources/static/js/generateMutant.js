$( document ).ready(function() {
	var IsBindingGen = false;

	$(window).bind("load", function() {
		// code here
		console.log("Initial Generate Test Result");
		var dataGrid = null;
		IsBindingGen = true;
		bindGenerateMutantResultToGrid(dataGrid);
	});

	$("#excelExport").click(function() {
		event.preventDefault();
		console.log("Export");
		$("#mutantTreeGrid").jqxTreeGrid('exportData', 'xls');
		//$("#mutantTreeGrid").jqxGrid('exportdata', 'xls', 'jqxGrid');
	});

	// SUBMIT FORM
    $("#generateMutant").click(function(event) {
		// Prevent the form from submitting via the browser.
		event.preventDefault();
		ajaxPost();
	});
    
    
    function ajaxPost(){
    	
    	// PREPARE FORM DATA
    	var formData = {
    		testItemId : $("#BPMNModel").children("option:selected").val(),
    		weakMutationType :  $("#WeakNutationLevel").children("option:selected").val()
    	}
		//alert(formData);
		console.log(formData);

		//data : JSON.stringify(formData),
		console.log(JSON.stringify(formData));

		waitingDialog.show('Generating Mutant: Please wait...');

		// DO POST
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "generateMutant/generate",
			data : JSON.stringify(formData),
			dataType : 'json',
			success : function(result) {
				// if(result.status == "Done"){
				// 	console.log("Return Result");
				// 	generateMutant(result);
				// }else{
				// 	//tmpResult = "error";
				// 	generateMutant(null);
				// }
				console.log(result);
				bindGenerateMutantResultToGrid(result);
				setTimeout(function () {
					waitingDialog.hide();
				}, 1000);

			},
			error : function(e) {
				//tmpResult = "error";
				setTimeout(function () {
					waitingDialog.hide();
				}, 1000);
				//alert(e.responseText);
				console.log("ERROR: ", e.responseText);
				Swal.fire({
					type: 'error',
					title: 'error...',
					text: e.responseText,
				  })
			}
		});
    	
    	// Reset FormData after Posting
    	//resetData();
		//generateMutant(rawResult);
    }
    
	function bindGenerateMutantResultToGrid(dataGrid){
		
		//alert(formData);
		console.log(dataGrid);

		//Call Get Data Source
		var source = {
			datatype: 'json',
			datafields: [{
				name: 'operator',
				map: "operator",
				type: 'string'
			}
			, {
				name: 'genFileName',
				map: "genFileName",
				type: 'string'
			}
			, {
				name: 'mutantTestItemDetail',
				map: "mutantTestItemDetail",
				type: 'array'
			}
			, {
				name: 'originalStatement',
				map: "originalStatement",
				type: 'string'
			}
			, {
				name: 'mutantStatement',
				map: "mutantStatement",
				type: 'string'
			}
			],
			hierarchy:
                {
					root: "mutantTestItemDetail"
                    //keyDataField: { name: 'operator' }
                    // parentDataField: { name: 'operator' }
                },
			//id: 'genFileName',
			localdata: dataGrid
		};

		//Create Data Source
		var dataAdapter = new $.jqx.dataAdapter(source);
		//Check ว่ามีข้อมูลใหม่ใน Grid หรือป่าว
		if (IsBindingGen === true) {
			// jqGrid exist
			console.log("reload grid" + IsBindingGen);
			// passing "cells" to the 'updatebounddata' method will refresh only the cells values when the new rows count is equal to the previous rows count.
			//$("#mutantTreeGrid").jqxTreeGrid('updatebounddata', 'cells');
			//$("#mutantTreeGrid").trigger("reloadGrid");
			$("#mutantTreeGrid").jqxTreeGrid('destroy');
			$('#mutantTreeGridContainer').after('<div id = "mutantTreeGrid" class="row justify-content-center"></div>');
			IsBindingGen = false;
		}

		IsBindingGen = true;
		console.log("new grid" + IsBindingGen);
		

		//Create Tree Grid
			$("#mutantTreeGrid").jqxTreeGrid({
				width: 1120,
				theme: 'arctic',
				source: dataAdapter,
				pageable: true,
				// ready: function() {
				// 	// expand row with 'EmployeeID = 2'
				// 	$("#treeGrid").jqxTreeGrid('expandRow', 2);
				// },
				columnsResize: true,
				pageSize: 200,
				columns: [{
					text: 'Operator',
					dataField: 'operator',
					align: "center",
					minWidth: 70,
					width: 70
				}, {
					text: 'Mutant Id',
					dataField: 'genFileName',
					width: 250
				}, {
					text: 'Original',
					dataField: 'originalStatement',
					width: 400
				}, {
					text: 'Mutant',
					dataField: 'mutantStatement',
					width: 400
				}]
			});
	}

	$('#generatedMutant').submit(function(e){                                   
		e.preventDefault();    
		console.log($('#generatedMutant').serialize());                  
		$.ajax({
			type: 'post'
		  , url: "/saveGeneratedMutant"
		  , data:  $('#generatedMutant').serialize()
		  , success: function(returnedData){
				console.log(returnedData); 
				//  /testExecution 
				$(location).attr('href',"/testExecution");
			}
		  , error: function(ex) {
				console.log(ex);
				Swal.fire({
					type: 'error',
					title: 'error...',
					text: ex.responseText,
				});
		    }
		});
	});
})