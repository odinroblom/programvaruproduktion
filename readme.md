# PVP21

## Technical details:
A Spring Boot application which connects the back and frontend via Dependency Injection.
An embedded on-file H2 database is used for persisting tables "sales" and "product_pricing".
Tests heavily utilize Mockito, but instead of mocking the database we use an in-memory H2 database.

## Instructions:

The program is run from the class SpringBootApp, which is located in the backend package. (src/main/java/backend/SpringBootApp.java)

WARNING! The third party applications (CardReader.jar, CashBox.jar, CustomerRegister.jar, and ProductCatalog.jar) need to be opened before running this program.

Upon startup all 4 user interfaces will show up at once.

    **GUI**:
        _Cashier_System_:
            This view is for the Cashier.
            On the right section of the view, the Cashier can add the products to the cart.
            In the search item textfield the Cashier can enter in the desired product.
                The program is automatically set to name, but with the "Search By" dropdown box the Cashier can then choose between Name, Barcode, or Keyword.
                If, for example, the Cashier chooses Keyword and types in a keyword, they can then press the checkmark box to the right and select the correct item from the "Select Item" dropdown menu.
                    The checkmark box makes it possible for the Cashier to find the desired products. They will then be displayed in the "Select Item" dropdown menu.

            When the correct item is selected, the Cashier can then enter the correct quantity and total price for the item chosen.
            If the item has a discount, or if the best before date is near, the Cashier can add a discount to the product as well.
                This can be edited afterwards as well.

            When the everything seems in order for that product, the Cashier can then press the "Add to Cart" button which will then add the chosen product to the table, Cart, down below.
                Each item will be shown with their Item name, quantity, and price.
            The "Remove Selected Item From Cart" will remove the selected item in the cart, when pressed.

            Bellow everything we have the Total Sum for the sale, including a textfield for the Cashier to enter in if the Customer wishes to pay with cash.
                It also displays how much cash to return to the Customer if they needed.
            On the left hand side we have the option for the Cashier to place a sale on hold if the Customer has forgotten their wallet etc.
                The Cashier enters a name for the current sale and presses the "Save" button.
                    This will save the sale in the dropdown menu underneath "Choose existing basket", and clears the cart for the next Customer to be served.
                    When the Cashier wishes to continue with a sale they can select the desired shelfed sale from the same dropdown menu, and press the "Choose" button to continue.

            And finally, we have the big "Pay" button. When pressed, the CardReader application will present the text "Waiting for payment" followed by the total sum from the sale.
                If the payment goes through without problems, the Cart will clear and display a successful transaction.
                If not, the view will display the error and the sale will remain until the Customer can provide a successful form of payment.
            

        _Customer_Screen_:
            This view is for the Customer.

            The only purpose of this window is for the Customer to view the scanned products, the total sum, and how much will be returned to them(if they payed with cash).
            The Customer can then ask the Cashier to add or remove items from the Cart.
            

        _Marketing_panel_:
            This view is for the Marketing department.
            We have user input section to the left, a tableview in the middle, and a barchart on the right.

            Left section:
                "Sale Time Period" date textfields.
                    The user can enter in the date time periods to view the desired data fromt the chosen time period. Date format is YYYY-MM-DD.
                "Buyer Birthday Between" date textfields.
                    The user can specify the ages of the customers by entering the birthday date ranges. Date format is YYYY-MM-DD.
                "Buyer Sex" dropdown menu
                    The user can choose to specify the sex of the customer data, or to keep it any. Options are ANY, Male, and Female.
                
                When the desired information is entered the "Load" button will display the data on the two viewing options.
                The "Clear Chart" button clears the chart of the data.

            Middle section:
                The tableview displays the desired information about the products from the chosen inputs.

            Right section:
                Barchart displaying the desired data for the marketing department to do their analysis and evaluations.


        _Sales_panel_:
            This view is for the Sales department.

            Left section:
                The user can enter in the desired date to view the data on the tableview below.
                The barcodes and quantities will be displayed in the tablview of the desired user input.

            Right section:
                Just as in the Cashier system, the "Search Item" textfield is open for user input.
                The "Search By" dropdown menu is to specify the item search; either by Name, Barcode, or Keyword.
                The checkmark box then utilizes the user input to then display the desired information in the tableview below.
