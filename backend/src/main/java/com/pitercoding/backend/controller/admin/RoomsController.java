package com.pitercoding.backend.controller.admin;

import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.services.admin.rooms.RoomsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class RoomsController {

    private final RoomsService roomsService;

    @PostMapping("/room")
    public ResponseEntity<?> postRoom(@RequestBody RoomDTO roomDTO) {
        boolean success = roomsService.postRoom(roomDTO);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
