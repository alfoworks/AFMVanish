package ru.allformine.afmvanish;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "afmvanish",
        name = "AFMVanish",
        description = "AFMVansh",
        authors = {
                "IteratorW",
                "HeroBrine1st Erquilenne"
        }
)
public class AFMVanish {
// кря
    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
