angular.module('CouponApp', ['ngResource'])
	.controller('CouponController', function($resource) {
		
	//CONTROLLER NAME
	var p = this;
	p.page_size = 5;
	p.exception_cause = {"lastPage":"LASTPAGE"};
	
	//LOGIN RESOURCES
	p.adminLoginResource = $resource("api/admin/login/:username/:password", {"username":"@username", "password":"@password"});
	p.companyLoginResource = $resource("api/company/login/:username/:password", {"username":"@username", "password":"@password"});
	p.customerLoginResource = $resource("api/customer/login/:username/:password", {"username":"@username", "password":"@password"});

	p.user = { 
			"username":"",
			"password":""
	};
	
	p.client =  { 
			"type":""
			};
		
	
	//VIEW BOOLEAN
	p.isLoggedIn = false;
	p.isAdmin = false;
	p.isCompany = false;
	p.isCustomer = false;
	
	p.adminLogin = function(){
		
		//if (type in clientTypes == "admin") {
		p.adminLoginResource.save(
				p.user,
				// on success
				function(response){
					p.isLoggedIn = true;
					p.isAdmin = true;
					p.info("Successful login");
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
	}
	p.companyLogin = function(){
		
		p.companyLoginResource.save(
				p.user,
				// on success
				function(response){
					p.isLoggedIn = true;
					p.isCompany = true;
					p.info("Successful login");
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
	}
	
	p.customerLogin = function(){ 
		p.customerLoginResource.save(
				p.user,
				function(response){
					p.isLoggedIn = true;
					p.isCustomer = true;
					p.info("Successful login");
				},
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
	}
	p.loginBtn = function() { 
		if(p.client.type==""){
			p.client.type=1;
		}
		
		if(p.client.type==1){
			p.customerLogin();
			p.client.type="";
		}
		if(p.client.type==2){
			p.companyLogin();
			p.client.type="";
		}
		if(p.client.type==3){
			p.adminLogin();
			p.client.type="";
		}

	}
	p.resetUser = function(){
		p.user = { 
				"username":"",
				"password":""
		};
	}

	p.adminLogout = function(){
		
		p.adminLoginResource.delete(
				// on success
				function(){
					p.isLoggedIn = false;
					p.isAdmin = false;
					p.resetAdmin();
					p.adminResetIncome();
					p.resetUser();
				}, 
				// on error
				function(response){
					p.handleError(response, "Failed to log out.");
				});
		
		
	}
	p.companyLogout = function(){
		p.companyLoginResource.delete(
				// on success
				function(){
					p.resetCoupons();
					p.isLoggedIn = false;
					p.isCompany = false;
					p.resetUser();
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
		
		
	}
	
	p.customerLogout = function(){
		p.customerLoginResource.delete(
				// on success
				function(){
					p.isLoggedIn = false;
					p.isCustomer = false;
					p.resetUser();
					p.resetCustomer();
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
		
		
	}
	//ADMIN RESOURCES FOR COMPANIES AND CUSTOMERS
	p.companies = $resource("api/admin/companies/:id", {id:'@id', 'byPage':'@byPage', 'size':'@size'},{
		// a custom method that invoked HTTP PUT
		"updateCompany": {method:"PUT"}
	});
	p.customers = $resource("api/admin/customers/:id", {id:'@id', 'byPage':'@byPage', 'size':'@size'},{
		// a custom method that invoked HTTP PUT
		"updateCustomer": {method:"PUT"}
	});
	
	
	//COMPANY RESOURCE FOR COUPON MANAGEMENT
	p.companyResource = $resource("api/company/:id", {'id': '@id', 'byType': '@byType','byPrice':'@byPrice', 'byPage' :'@byPage', 'size':'@size'}, {
		"updateCoupon": {method: "PUT"}	
		});
	
	p.adminCompPage = {"byPage": 0, "size": p.page_size};
	p.adminCustPage = {"byPage": 0, "size": p.page_size};
	p.adminShowComps = false;
	p.allCompanies = [];
	
	p.showComps = function(){
		var cLength = p.allCompanies.length;
		if(cLength>0){
			p.adminShowComps = false;
			p.hideCompanies();
		}else {
			p.adminShowComps = true;
			p.loadCompanies();
		}
	}
	p.adminCompPreviousPage = function(){
			p.adminCompPage.byPage--;
			p.pageError = "";
			if (p.adminCompPage.byPage<0){
				p.pageError = "Cannot leaf backward, first page reached."
				p.adminCompPage.byPage = 0;
			}
			p.loadCompanies();
		}
	p.adminCompNextPage = function() {
		p.adminCompPage.byPage++;
		p.pageError = "";
		p.loadCompanies();
	}
	p.loadCompanies = function(){
		p.allCompanies = p.companies.query(p.adminCompPage, function(){
			// add each company an edit attribute with the value false
			p.allCompanies.forEach(function(company){
				company.edit = false;
			});
			},
			function(response){
				if (response.data.cause == p.exception_cause.lastPage){
				p.handlePageError(response, "Last page reached.");
				p.adminCompPage.byPage--;
				p.loadCompanies();
				}else{
					p.handlePageError(response, "An Error has occurred.");
				}
			});
	} 
	p.hideCompanies = function() {
		p.allCompanies = [];
	}
	
	p.adminShowCusts = false;
	p.allCustomers = [];
	
	p.showCusts = function(){
		var cLength = p.allCustomers.length;
		if(cLength>0){
			p.adminShowCusts = false;
			p.hideCustomers();
		}else {
			p.adminShowCusts = true;
			p.loadCustomers();
		}
	}
	
	p.adminCustPreviousPage = function(){
		p.adminCustPage.byPage--;
		p.pageCustError = "";
		if (p.adminCustPage.byPage<0){
			p.pageCustError = "Cannot leaf backward, first page reached."
			p.adminCustPage.byPage = 0;
		}
		p.loadCustomers();
	}
p.adminCustNextPage = function() {
	p.adminCustPage.byPage++;
	p.pageCustError = "";
	p.loadCustomers();
}
	
	p.loadCustomers = function(){
		p.allCustomers = p.customers.query(p.adminCustPage, function(){
			p.allCustomers.forEach(function(customer){
				customer.edit = false;
			});
		},
		function(response){
		if (response.data.cause == p.exception_cause.lastPage){	
			p.handleAdminCustPageError(response, "Last page reached.");
			p.adminCustPage.byPage--;
			p.loadCustomers();
			}else{
				p.handleAdminCustPageError(response, "Error has occurred.");				
			}
		});
		}

	p.hideCustomers = function() {
		p.allCustomers = [];
	}
	
	p.removeCompany = function(company){
		company.$delete(function(){
			p.loadCompanies();
			p.info("successfully deleted company");
		}, // on error
		function(response){
			p.loadCompanies();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	p.removeCustomer = function(customer){
		customer.$delete(function(){
			p.loadCustomers();
			p.info("successfully deleted customer");
		}, // on error
		function(response){
			p.loadCustomers();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
		
	p.toggleUpdateCompany= function(company){
		company.edit = !company.edit;
	}
	p.toggleUpdateCustomer= function(customer){
		customer.edit = !customer.edit;
	}

	p.invokeUpdateCompany = function(company){
		const updateCompany = company;
		
		delete updateCompany.edit;
		
		updateCompany.$updateCompany(
			// on successful update 
			function(){
				p.loadCompanies();
				p.info("Successfully updated company");
			},
			// on error
			function(response){
				p.handleError(response, "Failed to update company.");
				p.loadCompanies();
				company.edit = true;
			}
		);
	}
	p.invokeUpdateCustomer = function(customer){
		const updateCustomer = customer;
		
		delete updateCustomer.edit;
		
		updateCustomer.$updateCustomer(
				// on successful update 
				function(){
					p.loadCustomers();
					p.info("Successfully updated customers");
				},
				// on error
				function(response){
					p.loadCustomers();
					p.handleError(response, "Exception thrown on client side.");
				}
		);
	}
	
	p.loadCompany = function(company){
		company.$get(function(item){
			company.id = item.id;
			company.compName = item.compName;
			company.password=item.password;
			company.email=item.email;
			company.edit = true;
			p.info("sucessfully reloaded company");
		}, // on error
		function(response){
			p.loadCompanies();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	p.loadCustomer = function(customer){
		customer.$get(function(item){
			customer.id = item.id;
			customer.password = item.password;
			customer.custName = item.custName;
			customer.edit = true;
			p.info ("sucessfully reloaded customer");
		}, // on error
		function(response){
			p.loadCustomers();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	
	p.Company = {"compName":"", "password":"","email": ""};

	p.showNewCompForm = false;
	p.createCompany = function(){
		p.clearCompForm();
		p.errorMessage = "";
		p.showNewCompForm = !p.showNewCompForm;
	}
	p.clearCompForm = function() {
		p.Company.compName = "";
		p.Company.password = "";
		p.Company.email = "";
		p.createCompInfo = "";
		p.createCompError = "";
	}
	
	p.invokeCreateCompany = function(){
		p.createCompInfo = "";
		p.createCompError = "";
		p.companies.save(
			p.Company,
			function(){
				p.Company.compName = "";
				p.Company.password = "";
				p.Company.email = "";
				p.createCompInfo = "Successfully created new company";
			},
			function(response){
				p.handleError(response, "Failed to create new Company.");
				p.handleCreateCompError(response, "Failed to create new Company.");
			});
	}
	
	p.Customer = {"custName":"", "password":""};

	p.showNewCustForm = false;
	p.createCustomer = function(){
		p.clearCustForm();
		p.errorMessage = "";
		p.showNewCustForm = !p.showNewCustForm;
	}
	p.clearCustForm = function() {
		p.Customer.custName = "";
		p.Customer.password = "";
		p.createCustInfo = "";
		p.createCustError = "";
	}
	
	p.invokeCreateCustomer = function(){
		p.createCustInfo = "";
		p.createCustError = "";
		p.customers.save(
			p.Customer, 
			function(){
				p.Customer.custName = "";
				p.Customer.password = "";
				p.createCustInfo = "Successfully created new customer.";
			},
			// on error
			function(response){
				p.handleError(response, "Failed to create new customer.");
				p.handleCreateCustError(response, "Failed to create new customer.");
			});
	}
	p.resetAdmin = function(){
		p.adminCompPage = 0;
		p.showNewCustForm = false;
		p.showNewCompForm = false;
		p.adminShowComps = false;
		p.adminShowCusts = false;
		p.pageError = "";
		p.pageCustError = "";
		p.allCompanies = [];
		p.allCustomers = [];
	}
	
	//COMPANY METHODS
	
	p.allCompanyCoupons = [];
	p.comparams =
					{"byType":"", "byPrice":"", "byPage":"0", "size": p.page_size};
	p.isManageCpnPressed = false;
	
	p.compCpnNextPage = function(){	
			p.comparams.byPage++;
			p.pageError = "";
			p.loadCompanyCoupons();
	}
	p.compCpnPreviousPage = function(){	
		p.comparams.byPage--;
		p.pageError = "";
		if (p.comparams.byPage<0){
			p.pageError = "Cannot leaf backward, first page reached."
			p.comparams.byPage = 0;
		}
		p.loadCompanyCoupons();
	}
	
//	p.loadCustomers = function(){
//		p.allCustomers = p.customers.query(p.adminCustPage, function(){
//			// add each company an edit attribute with the value false
//			p.allCustomers.forEach(function(customer){
//				customer.edit = false;
//			});
//		},
//		function(response){
//		if (response.data.cause == p.exception_cause.lastPage){	
//			p.handleAdminCustPageError(response, "Last page reached.");
//			p.adminCustPage.byPage--;
//			p.loadCustomers();
//			}else{
//				p.handleAdminCustPageError(response, "Error has occurred.");				
//			}
//		});
//		}


	p.loadCompanyCoupons = function(){
		p.allCompanyCoupons = p.companyResource.query(p.comparams, function(){
			p.allCompanyCoupons.forEach(function(coupon){
				coupon.edit = false;
			});
		}, 
		function(response){
			if (response.data.cause == p.exception_cause.lastPage){	
				p.handlePageError(response, "Last page reached.");
				p.comparams.byPage--;
				p.loadCompanyCoupons();
				}else{
					p.handlePageError(response, "Error has occurred.");				
				}
		});
	
	} 
	p.manageCpnBtn = function(){
		var cpnLength = p.allCompanyCoupons.length;
			p.loadCompanyCoupons();
			p.isManageCpnPressed = true;
		}
	p.resetCoupons = function(){
		p.isManageCpnPressed = false;
		p.showCreateCpn = false;
		p.allCompanyCoupons = [];
	}

	p.hideCompanyCoupons = function() {
		p.allCompanyCoupons = [];
		p.pageError = "";
		p.isManageCpnPressed = false;
	}
	p.showCreateCpn = false;
	
	p.createCpn = function(){
		p.showCreateCpn = !p.showCreateCpn;
	}
	
	p.invokeCreateCoupon = function(){
		p.companyResource.save(
		p.Coupon,
		function() {
			p.Coupon.title = "";
			p.Coupon.startDate = "";
			p.Coupon.endDate = "";
			p.Coupon.amount = "";
			p.Coupon.type = "";
			p.Coupon.message = "";
			p.Coupon.price = "";
			p.Coupon.image = "";
			p.compGetIncome();
			p.info("Successfully created Coupon");
			p.loadCompanyCoupons;
		}, function(response){
			p.loadCompanyCoupons();
			p.handleCreateError(response, "Exception thrown on client side.");
		}
		);
	}
	
	p.removeCoupon = function(coupon){
		coupon.$delete(function(){
			p.loadCompanyCoupons();
			p.info("successfully deleted coupon");
		}, // on error
		function(response){
			p.loadCustomers();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	p.toggleUpdateCoupon = function(coupon){
		coupon.edit= !coupon.edit;
	}
	
	p.invokeUpdateCoupon = function(coupon){
		const updateCoupon = coupon;
		
		delete updateCoupon.edit;
		
		updateCoupon.$updateCoupon(
				// on successful update 
				function(){
					p.loadCompanyCoupons();
					p.compGetIncome();
					p.info("Successfully updated coupon.");
				},
				// on error
				function(response){
					p.loadCoupon();
					p.handleError(response, "Exception thrown on client side.");
				}
		);
	}
	
	p.loadCoupon = function(coupon){
		coupon.$get(function(item){
			coupon.id = item.id;
			customer.title = item.title;
			customer.amount = item.amount;
			customer.price = item.price;
			customer.message = item.message;
			customer.startDate = item.startDate;
			customer.endDate = item.endDate;
			customer.type = item.type;
			customer.image = item.image;
			customer.edit = true;
			p.info("sucessfully reloaded coupon");
		}, // on error
		function(response){
			p.loadCompanyCoupons();
			p.handleError(response, "Exception thrown on client side.");
		});
	}


	
	
	 
	 
	 
	 //CUSTOMER RESOURCE FOR MANAGING OWNED COUPONS
	 p.customerManageResource = $resource("api/customer/:id", {'id': '@id', 'byType': '@byType','byPrice':'@byPrice', 'byPage' :'@byPage', 'size':'@size'});
	 //CUSTOMER RESOURCE FOR COUPON BROWSING&PURCHASING
	 p.customerPurchaseResource = $resource("api/customer/purchase/:id", {'id': '@id', 'byType': '@byType','byPrice':'@byPrice', 'byPage' :'@byPage', 'size':'@size'});

	 p.customerManageParams = {"size": p.page_size, "byPage": 0, "byType":"", "byPrice":""};
	 p.customerPurchaseParams = {"size": 6, "byPage": 0, "byType":"", "byPrice":""};
	
	 //CUSTOMER METHODS:
	 p.resetCustomer = function(){
		 p.pageError = "";
		 p.custManagePageError = "";
		 p.allExistingCoupons = [];
		 p.allPurchasedCoupons = [];
		 p.showPurchasedDiv = false;
		 p.showPurchaseDiv = false;
		 resetCustomerIncome();
		 p.customerManageParams.byPage = 0;
		 p.customerPurchaseParams.byPage = 0;
	 }
	 
	 p.allExistingCoupons = [];
	 p.allPurchasedCoupons = []
	 p.showPurchasedDiv = false;	
	 
	 
p.showPurchasedCpnBtn = function(){
	p.pageError = "";
	p.showPurchasedDiv = !p.showPurchasedDiv;
		 if(p.showPurchasedDiv){
			 p.loadPurchasedCoupons();
		 }else {
			 p.allPurchasedCoupons = [];
			 }
	 };
	 p.loadPurchasedCoupons = function(){
		 p.allPurchasedCoupons = p.customerManageResource.query(p.customerManageParams, function(){
			 p.allPurchasedCoupons.forEach(function(coupon){
				 coupon.details = false;
			 }
			 );
			 p.info("succesfully loaded coupons");
		 },
		 function(response){
			 if (response.data.cause == p.exception_cause.lastPage){	
					p.handleCustManagePageError(response, "Last page reached.");
					p.customerManageParams.byPage--;
					p.loadPurchasedCoupons();
			 }else{
				 p.handleCustManagePageError(response, "Cannot turn page.");				 
			 }
		 });
	 }
	 p.purchasedCouponsNextPage = function(){
		 p.customerManageParams.byPage++;
			p.custManagePageError = "";
			p.loadPurchasedCoupons();
}
			 
	 
	 p.purchasedCouponsPreviousPage = function(){
		 p.customerManageParams.byPage--;
		 p.custManagePageError = "";
			if (p.customerManageParams.byPage<0){
				p.custManagePageError = "Cannot leaf backward, first page reached."
				p.customerManageParams.byPage = 0;
			}
			p.loadPurchasedCoupons();
			}
	 p.showPurchaseDiv = false;
	 
	 p.showAllCpnBtn = function(){ 
		 var cpnLength = p.allExistingCoupons.length;
		 if (cpnLength>0){
			 p.hideAllCoupons();
			 p.showPurchaseDiv = false;
			 } else if(cpnLength==0){
			 p.loadAllCoupons();
			 p.showPurchaseDiv = true;
		 }
		 }
	 
	 p.loadAllCoupons = function(){
			p.allExistingCoupons = p.customerPurchaseResource.query(p.customerPurchaseParams, function(){
				p.allExistingCoupons.forEach(function(coupon){
					coupon.details = false;
				});
				p.info("succesfully loaded coupons");
			}, 
			function(response){
				if (response.data.cause == p.exception_cause.lastPage){	
					p.handlePageError(response, "Last page reached.");
					p.customerPurchaseParams.byPage--;
					p.loadAllCoupons();
					}else{
						p.handlePageError(response, "Error has occurred.");				
					}
			});
		
		} 
	
	
	 p.allCouponsNextPage = function(){
		 p.customerPurchaseParams.byPage++;
			p.pageError = "";
			p.loadAllCoupons();
}
			 
	 
	 p.allCouponsPreviousPage = function(){
		 p.customerPurchaseParams.byPage--;
			p.pageError = "";
			if (p.customerPurchaseParams.byPage<0){
				p.pageError = "Cannot leaf backward, first page reached."
					 p.customerPurchaseParams.byPage = 0;
			}
			p.loadAllCoupons();
			}
	 
	 p.hideAllCoupons = function() {
			p.allExistingCoupons = [];
			 p.showPurchaseDiv = false;
		}
	 
	 
	 p.toggleCouponDetails = function(coupon){
		 coupon.details = !coupon.details;
	 }
	
	 p.purchaseCoupon = function(coupon){
		 delete coupon.details;
				 coupon.$save(
					function(){
						p.info("Successfully purchased coupon");
						p.loadAllCoupons();
						 p.loadPurchasedCoupons();
					}, 
					// on error
					function(response){
						p.handleError(response, "Exception thrown on client side.");
					});
	 }
	 
	 
		//SPRING RELATED CODE
		
		p.adminViewIncome = $resource("/api/admin/income", {"byCustomer":"@byCustomer", "byCompany":"@byCompany"});
		
		p.adminResetIncome = function(){
			p.showIncome = false;
			p.adminIncome = {"byCustomer":"", "byCompany":""};
			p.allCustomerIncome = []
			p.allCompanyIncome = [];
		}
		
		p.showIncome = false;
	p.adminIncome = {"byCustomer":"", "byCompany":""};
	p.allCustomerIncome = [];
	p.allCompanyIncome = [];

	p.adminIncomeBtn = function(){
		p.showIncome = !p.showIncome;
		if (p.showIncome){
			p.getCustomerIncome();
		}else {
			p.allCustomerIncome = [];
		}
	}
	
	p.getCustomerIncome = function(){
		p.allCustomerIncome = [];
		 p.allCustomerIncome = p.adminViewIncome.query(p.adminIncome, function(){
			p.allCustomerIncome.forEach(function(income){
				
			});
			p.info("loaded income.");
		},
		function(response){
			p.handleError(response, "Exception thrown on client side");
		});
	}
	p.getCompanyIncome = function(){CompIncome.byCompany,
		p.allCompanyIncome = p.adminViewIncome.query(function(){
			
			p.allCompanyIncome.forEach(function(income){
			});
			p.info("succesfully loaded coupons");
		},
		function(response){
			p.handleError(response, "Exception thrown on client side");
		});
	}
	p.companyViewIncome = $resource("/api/company/income");
	p.customerViewIncome = $resource("/api/customer/income");
	
	p.compIncome = [];
	p.custIncome = [];
	p.showCompInc = false;
	p.compIncBtn = function() { 
		var incLength = p.compIncome.length;
		if (incLength>0){
			p.compIncome = [];
			p.showCompInc = false;
		}else{
			p.compGetIncome();
			p.showCompInc = true;
		}
		
	}
	p.compGetIncome = function(){
		 p.compIncome = p.companyViewIncome.query(function(){
			p.compIncome.forEach(function(income){
				
			});
			p.info("loaded income.");
		},
		function(response){
			p.handleError(response, "Exception thrown on client side");
		});
	}
	
	p.showCustIncome = false;
	
	p.custGetIncome = function(){
		 p.custIncome = p.customerViewIncome.query(function(){
				p.custIncome.forEach(function(income){
					
				});
				p.info("loaded income.");
			},
			function(response){
				p.handleError(response, "Exception thrown on client side");
			});
		}
	p.custIncomeBtn = function(){
		var incLength = p.custIncome.length;
		if(incLength>0){
			p.custIncome=[];
			p.showCustIncome = false;
		}else{
			p.custGetIncome();
			p.showCustIncome = true;
		}
	}
	 resetCustomerIncome = function(){
		 p.custIncome=[];
		 p.showCustIncome = false;
	 }
	 
	 
	 
//END OF SPRING
	
	//SUCCESS MESSAGES
	p.infoMessage  = "";
	p.createCustInfo = "";
	p.createCompInfo = "";
	p.createCouponInfo = "";
	p.updateCompInfoMessage = "";
	
	
	//ERROR HANDLING
	p.pageCustError = "";
	p.loginError = "";
	p.pageError = "";
	p.purchaseError = "";
	p.errorMessage = "";
	p.createCustError = "";
	p.createCompError = "";
	p.createCouponError = "";
	p.updateCompError  = "";
	p.custManagePageError = "";
	
	p.info = function(message){
		p.infoMessage  = message;
		p.errorMessage = "";
	}
	p.updateCompInfo = function(message){
		p.updateCompInfoMessage  = message;
		p.updateCompError = "";
	}
	
	p.error = function(message){
		p.infoMessage  = "";
		p.errorMessage = message;
	}
	p.updateCompError = function(message){
		p.updateCompInfoMessage  = "";
		p.updateCompError = message;
	}
	p.handlePageError =  function(response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.pageError =  response.data.message;
		}else{
			p.pageError = defaultMessage; 
		}
	}
	p.handleCustManagePageError =  function(response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.custManagePageError =  response.data.message;
		}else{
			p.custManagePageError = defaultMessage; 
		}
	}
	p.handleAdminCustPageError =  function(response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.pageCustError =  response.data.message;
		}else{
			p.pageCustError = defaultMessage; 
		}
	}
	p.handleUpdateCompError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.updateCompError =  response.data.message;
			p.updateCompInfoMessage = "";
		}else{
			p.updateCompError = defaultMessage; 
			p.updateCompInfoMessage = "";
		}
	}
	
	p.handleError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.error(response.data.message);
		}else{
			p.error(defaultMessage); 
		}
	}
	p.handleLoginError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.loginError = response.data.message;
		}else{
			p.loginEerror = defaultMessage; 
		}
	}
	p.handleCreateCustError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.createCustError = response.data.message;
		}else{
			p.createCustError = defaultMessage; 
		}
	}
	p.handleCreateCompError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.createCompError =  response.data.message;
		}else{
			p.createCompError = defaultMessage; 
		}
	}
	p.handleCreateCouponError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.createCouponError =  response.data.message;
		}else{
			p.createCouponError = defaultMessage; 
		}
	}
	

});
