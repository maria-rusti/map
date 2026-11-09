package domain;

import java.time.LocalDateTime;

public class Prietenie extends Entity<Tuple<Long, Long>> {
    private LocalDateTime friendsFrom;

    public Prietenie(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public Prietenie() {
        this.friendsFrom = LocalDateTime.now();
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }
}