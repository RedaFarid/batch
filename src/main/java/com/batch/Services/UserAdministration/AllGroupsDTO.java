package com.batch.Services.UserAdministration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllGroupsDTO {
    private List<WindowGroupsDTO> list = new ArrayList<>();
}
