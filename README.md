# Grocery Store App - Android (Java)

**Grocery Store** is a Java-based Android application that allows users to browse, search, and purchase grocery and food items. The app features a clean interface and supports common shopping functionalities.

## Main Features (Activities & Fragments)

| **Activity**                    | **Fragment**           | **Feature (Description)**                                                             |
|--------------------------------|------------------------|----------------------------------------------------------------------------------------|
| **SplashActivity**             | -                      | Initial launch screen to check login status                                           |
| **LoginActivity**              | -                      | User login with registered account, forgot password support                           |
| **RegisterActivity**           | -                      | Allows new users to register                                                          |
| **MainActivity**               | HomeFragment           | Home screen displaying banners and featured products                                  |
|                                | ProfileFragment        | Shows user profile info, change password, logout                                      |
|                                | WishlistFragment       | Displays list of products added to wishlist                                           |
|                                | SearchFragment         | Displays product list by search and filter criteria                                   |
| **FilterActivity**             | -                      | Supports product filtering by category/criteria                                       |
| **ProductDetailActivity**      | -                      | Shows product detail: name, price, images, ratings, wishlist/cart actions             |
| **CartActivity**               | -                      | Manages products added to the cart and allows selection for checkout                  |
| **OrderActivity**              | -                      | Displays user's order history with status tracking                                    |
| **OrderDetailActivity**        | -                      | Shows specific order detail: products, quantity, total cost                           |
| **PersonalActivity**           | -                      | Allows users to update personal account information                                   |
| **VnPayPaymentActivity**       | -                      | Displays VNPAY payment screen and handles the payment process                         |
| **CheckoutConfirmationActivity**| -                     | Confirms checkout, lets users choose between VNPAY or cash payment                    |



## Tech Stack

- **Language**: Java  
- **IDE**: Android Studio  
- **Architecture**: Updating  
- **Backend**: REST API (Spring boot)
