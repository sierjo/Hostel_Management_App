package com.hotel.Hotel.service.impl;

import com.hotel.Hotel.dto.Response;
import com.hotel.Hotel.dto.RoomDTO;
import com.hotel.Hotel.entity.Room;
import com.hotel.Hotel.exception.OurException;
import com.hotel.Hotel.repo.BookingRepository;
import com.hotel.Hotel.repo.RoomRepository;
import com.hotel.Hotel.security.AwsS3Service;
import com.hotel.Hotel.service.interfac.IRoomService;
import com.hotel.Hotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {

        Response response = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(String.valueOf(roomPrice));
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoom(roomDTO);
        } catch (OurException e) {

        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomType() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {

        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoomList(roomDTOList);
        } catch (OurException e) {

        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response deleteRoom(Long roomId) {

        Response response = new Response();

        try {
            roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            roomRepository.deleteById(roomId);
            response.setStatusCOde(200);
            response.setMessage("seccessful");
        } catch (OurException e) {
            response.setStatusCOde(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {

        Response response = new Response();

        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(String.valueOf(roomPrice)); // 3 21 00 Посмотреть если вдркг ошибка
            if (description != null) room.setRoomDescription(description);
            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCOde(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {

        Response response = new Response();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);

            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCOde(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {

        Response response = new Response();

        try {
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {

        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

            response.setStatusCOde(200);
            response.setMessage("seccessful");
            response.setRoomList(roomDTOList);

        } catch (OurException e) {
            response.setStatusCOde(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCOde(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }
}
