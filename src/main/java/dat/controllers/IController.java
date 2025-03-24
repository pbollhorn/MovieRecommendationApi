package dat.controllers;

import io.javalin.http.Context;

public interface IController
{
    void create(Context ctx);
    void getById(Context ctx);
    void getAll(Context ctx);
    void update(Context ctx);
    void delete(Context ctx);
}
