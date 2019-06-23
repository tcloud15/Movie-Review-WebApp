/*
Team name: Green
Team members: Walker LaVoy, Jason Rowland, Shakah Alhamed, Van Nguyen
Date: 05/15/2018
CSCI 367 - Spring 2018
Project: Movie review web application
Description: There are three main types of user, member, admin and visitor. Visitor can only search movie and read
  all the information related to the movie. Member can write a review to a movie, delete or edit their own reviews, and
  change their account password. Admin is a subclass of member, they have additional privileges like add movie/actor/genre/acts in/describes,
  edit/delete movie, and delete all members' review.
Deliverable 5
Instruction: Run Main in IntelliJ IDEA
Source code: Professor Wolff, class slides, class exercises
 */

import java.sql.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.Map;
import spark.Spark;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;


public class Main{

  public static void main(String[] args){

    ExampleController controller = new ExampleController();

    //Homepage
    Spark.get("/homepage", controller::getHomepage);

    //Search result for visitor
    Spark.post("/siteVisitor-search", controller::postSearchResult);

    //Movie report for visitor
    Spark.get("/visitor-movie-report/:name/:date/:dateuk", controller::visitorMovieReport);

    //Signup
    Spark.get("/sitemember-signup", controller::signup);
    Spark.post("/sitemember-signup", controller::postSignup);

    //Login
    Spark.get("/login", controller::login);
    //Authentication after logging in (display main menu for member/admin)
    post( "/authenticate", controller::postLoginForm);

    //Go to member's home (main menu)
    get("/user/home", controller::getUserHome );
    //Go to admin's home (main menu
    get("/admin/home", controller::getAdminHome );

    //Check authentication
    before("/user/*", controller::userBefore );
    before("/admin/*", controller::adminBefore );

    //Search result for member
    post("/user/user-search", controller::postSearchResult);
    //Search result for admin
    post("/admin/admin-search", controller::postSearchResult);

    //Movie report for member
    get("/user/member-movie-report/:name/:date/:dateuk", controller::memberMovieReport);
    //Movie report for admin
    get("/admin/member-movie-report/:name/:date/:dateuk", controller::memberMovieReport);

    //Add movie (admin only)
    get("/admin/add-movie", controller::addMovieForm);
    post("/admin/add-movie", controller::addMovie);

    //Add actor (admin only)
    get("/admin/add-actor", controller::addActor);
    post("/admin/add-actor", controller::actorAdded);

    //Add genre (admin only)
    get("/admin/add-genre", controller::addGenre);
    post("/admin/add-genre", controller::genreAdded);

    //Add actsin (admin only)
    get("/admin/add-actsin", controller::addActsIn);
    post("/admin/add-actsin", controller::actsInAdded);

    //Add describes (admin only)
    get("/admin/add-describes", controller::addDescribes);
    post("/admin/add-describes", controller::describesAdded);

    //Edit movie (admin only)
    get("/admin/edit-movie", controller::getEditMovie);
    post("/admin/edit-movie", controller::editMovie);

    //Delete all reviews (admin only)
    get("/admin/delete-review", controller::deleteReview);
    post("/admin/delete-review", controller::deleteReview);

    //Delete member's review only
    get("/user/delete-review", controller::deleteReview);
    post("/user/delete-review", controller::deleteReview);

    //Delete movie (admin only)
    get("/admin/delete-movie", controller::deleteMovie);
    post("/admin/delete-movie", controller::deleteMovie);

    //Change password (admin)
    get("/admin/change-password", controller::changePassword);
    post("/admin/change-password", controller::changePassword);

    //Change password (member)
    get("/user/change-password", controller::changePassword);
    post("/user/change-password", controller::changePassword);

    //Edit member's review only
    get("/user/edit-review", controller::getEditReview);
    post("/user/edit-review", controller::editReview);

    //Edit admin's review only
    get("/admin/edit-review", controller::getEditReview);
    post("/admin/edit-review", controller::editReview);
  }

  public static String renderTemplate(Map<String, Object> data, String path){
    ModelAndView modAndView = new ModelAndView(data, path);
    return new HandlebarsTemplateEngine().render(modAndView);
  }
}
