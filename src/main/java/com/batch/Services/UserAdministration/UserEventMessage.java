package com.batch.Services.UserAdministration;

import com.batch.Database.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventMessage {
    private boolean isLoggedOn;
    private User user;
    private AllGroupsDTO allGroupsDTO;
}
