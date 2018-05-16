angular.module('CouponApp', ['ngResource'])
	.controller('CouponController', function($resource) {
		
	//CONTROLLER NAME
	var p = this;
	
	//LOGIN RESOURCES
	p.adminLoginResource = $resource("api/admin/login/:username/:password", {"username":"@username", "password":"@password"});
	p.companyLoginResource = $resource("api/company/login/:username/:password", {"username":"@username", "password":"@password"});
	p.customerLoginResource = $resource("api/customer/login/:username/:password", {"username":"@username", "password":"@password"});

	p.user = { 
			"username":"",
			"password":""
	};
	
	p.clientTypes =  [ "admin", "company", "customer"];
		
	
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
		
		//if (type in clientTypes == "admin") {
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
	
	p.adminLogout = function(){
		p.adminLoginResource.delete(
				// on success
				function(){
					p.isLoggedIn = false;
					p.isAdmin = false;
					p.user = { 
							"username":"",
							"password":""
					};
					p.info("Successful logout");
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
		
		
	}
	p.companyLogout = function(){
		p.companyLoginResource.delete(
				// on success
				function(){
					p.isLoggedIn = false;
					p.isCompany = false;
					p.user = { 
							"username":"",
							"password":""
					};
					p.info("Successful logout");
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
					p.user = { 
							"username":"",
							"password":""
					};
					p.info("Successful logout");
				}, 
				// on error
				function(response){
					p.handleError(response, "Exception thrown on client side.");
				});
		
		
	}
	//ADMIN RESOURCES FOR COMPANIES AND CUSTOMERS
	p.companies = $resource("api/admin/companies/:id", {id:'@id'},{
		// a custom method that invoked HTTP PUT
		"updateCompany": {method:"PUT"}
	});
	p.customers = $resource("api/admin/customers/:id", {id:'@id'},{
		// a custom method that invoked HTTP PUT
		"updateCustomer": {method:"PUT"}
	});
	
	
	//COMPANY RESOURCE FOR COUPON MANAGEMENT
	p.companyResource = $resource("api/company/:id", {'id': '@id', 'byType': '@byType'}, {
	"updateCoupon": {method: "PUT"}	
	});
	
	
	
	p.allCompanies = [];
	
	p.loadCompanies = function(){
		p.allCompanies = p.companies.query(function(){
			// add each company an edit attribute with the value false
			p.allCompanies.forEach(function(company){
				company.edit = false;
			});
		});
	} 
	p.hideCompanies = function() {
		p.allCompanies = [];
	}
	
	p.allCustomers = [];
	
	p.loadCustomers = function(){
		p.allCustomers = p.customers.query(function(){
			// add each company an edit attribute with the value false
			p.allCustomers.forEach(function(company){
				customer.edit = false;
			});
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
				p.loadCompanies();
				p.handleError(response, "Exception thrown on client side.");
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
			p.info("sucessfully reloaded customer");
		}, // on error
		function(response){
			p.loadCustomers();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	
	p.Company = {"compName":"", "password":"","email": ""};

	
	p.invokeCreateCompany = function(){
		p.companies.save(
			p.Company,
			// on success
			function(){
				p.Company.compName = "";
				p.Company.password = "";
				p.Company.email = "";
				p.loadCompanies();
				p.info("Successfully created new company");
			},
			// on error
			function(response){
				p.loadCompanies();
				p.handleError(response, "Exception thrown on client side.");
			});
	}
	
	p.Customer = {"custName":"", "password":""};

	
	p.invokeCreateCustomer = function(){
		p.customers.save(
			p.Customer, 
			// on success
			function(){
				p.Customer.custName = "";
				p.Customer.password = "";
				p.loadCustomers();
				p.info("Successfully created new customer.");
			},
			// on error
			function(response){
				p.loadCustomers();
				p.handleError(response, "Exception thrown on client side.");
			});
	}
	
	
	p.allCompanyCoupons = [];
	p.byType = "";
	
//	p.loadCompanyCouponsByType = function(){
//		alert("p.loadCompanyCouponsByType()");
//		p.allCompanyCoupons = p.companyResource.query(function(){
////		p.byType.byType,
//			p.allCompanyCoupons.forEach(function(coupon){
//				coupon.edit = false;
//			});
//		});
//	} 
	function testCC() { 
		alert("hello world");
	}
	
	p.loadCompanyCoupons = function(){
		alert("hi");
		p.allCompanyCoupons = p.companyResource.query(
				p.byType,
				function(){
					console.log(p.byType);
					alert("hi");
			p.allCompanyCoupons.forEach(function(coupon){
				coupon.edit = false;
			});
		});
	} 
	p.hideCompanyCoupons = function() {
		p.allCompanyCoupons = [];
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
			p.info("Successfully created Coupon");
			p.loadCompanyCoupons();
		}, function(response){
			p.loadCompanyCoupons();
			p.handleError(response, "Exception thrown on client side.");
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

	
	 p.CouponTypes = ["Tech", "Lifestyle", "Electronics","Kitchen"];
	
	 
	 
	 
	 //CUSTOMER RESOURCE FOR MANAGING OWNED COUPONS
	 p.customerManageResource = $resource("api/customer/client/:id", {id: '@id'});
	 //CUSTOMER RESOURCE FOR COUPON BROWSING&PURCHASING
	 p.customerPurchaseResource = $resource("api/customer/purchase/:id", {id: '@id'});
	 p.customerBackPagePurchaseResource = $resource("api/customer/pagebackward/:id?:size=size&page=:page", {id: '@id'}, {
		 "updateCoupon": {method: "PUT"}	
	 });
	 p.pagination = {"size": 15, "page": 0};
	 p.customerNextPagePurchaseResource = $resource("api/customer/pageforward/:id", {id: '@id'}, {
		 "updateCoupon": {method: "PUT"}	
	 });
	 //CUSTOMER METHODS:
	 
	 p.allExistingCoupons = [];
	 p.allPurchasedCoupons = []
	 
	 //GET ALL COUPONS

	 p.loadPurchasedCoupons = function(){
		 p.allPurchasedCoupons = p.customerManageResource.query(function(){
			 p.allPurchasedCoupons.forEach(function(coupon){
				 coupon.details = false;
			 }
			 );
			 p.info("succesfully loaded coupons");
		 },
		 function(response){
			 p.handleError(response, "Exception thrown on client side");
		 });
	 }
	 p.loadAllCoupons = function(){
			p.allExistingCoupons = p.customerPurchaseResource.query(function(){
				p.allExistingCoupons.forEach(function(coupon){
					coupon.details = false;
				});
				p.info("succesfully loaded coupons");
			},
			function(response){
				p.handleError(response, "Exception thrown on client side");
			});
		}
	
	 p.loadCouponsNextPage = function(){
		 p.allExistingCoupons = p.customerNextPagePurchaseResource.query(function(){
			 p.allExistingCoupons.forEach(function(coupon){
				 coupon.details = false;
			 });
				 p.info("succesfully loaded coupons");
				},
				function(response){
					p.handleError(response, "Exception thrown on client side");
				});
			}
			 
	 
	 p.loadCouponsBackPage = function(){
		 p.allExistingCoupons = p.customerBackPagePurchaseResource.query(function(){
			 // add each company an edit attribute with the value false
			 p.allExistingCoupons.forEach(function(coupon){
				 coupon.details = false;
			 });
			 p.info("succesfully loaded coupons");
			},
			function(response){
				p.handleError(response, "Exception thrown on client side");
			});
	 }
	 
	 p.hideAllCoupons = function() {
			p.allExistingCoupons = [];
		}
	 
	 
	 p.toggleCouponDetails = function(coupon){
		 coupon.details = !coupon.details;
	 }
	 p.purchaseTest = function(coupon){
		 coupon.$get(function(item){
		 
		 p.info("sucessfully reloaded coupon");
		}, // on error
		function(response){
			p.loadCompanyCoupons();
			p.handleError(response, "Exception thrown on client side.");
		});
	}
	 p.purchaseCoupon = function(coupon){
		 delete coupon.details;
//		 p.customerPurchaseResource.save(coupon,
				 coupon.$save(
//					p.Coupon,
					// on success
					function(){
						p.info("Successfully purchased coupon");
						 p.loadPurchasedCoupons();
					}, 
					// on error
					function(response){
						p.handleError(response, "Exception thrown on client side.");
					});
	 }
	
	 
	 
	 
	 
	p.infoMessage  = "";
	p.errorMessage = "";
	
	p.info = function(message){
		p.infoMessage  = message;
		p.errorMessage = "";
	}
	
	p.error = function(message){
		p.infoMessage  = "";
		p.errorMessage = message;
	}

	p.handleError = function (response, defaultMessage){
		if(response.data != null && response.data.message != null){
			p.error("Error: " + response.data.message);
		}else{
			p.error(defaultMessage); 
		}
	}
	//SPRING RELATED CODE
	
	p.adminViewIncome = $resource("/api/admin/income", {"byCustomer":"@byCustomer", "byCompany":"@byCompany"});
	
	
p.CustIncome = {"byCustomer":""};
p.CompIncome = {"byCompany":""};
p.allCustomerIncome = [];
p.allCompanyIncome = [];

p.getCustomerIncome = function(){
	 p.allCustomerIncome = p.adminViewIncome.query(function(){
		 p.CustIncome.byCustomer,
		p.allCustomerIncome.forEach(function(income){
			
		});
		p.info("loaded income."");
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
	
	

});
