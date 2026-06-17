package com.cfs.BMS.service;

import com.cfs.BMS.dto.TheaterRequest;
import com.cfs.BMS.entity.City;
import com.cfs.BMS.entity.Theater;
import com.cfs.BMS.repository.CityRepository;
import com.cfs.BMS.repository.TheaterRepository;
import com.cfs.BMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterService {

  private final TheaterRepository theaterRepository;
  private final CityService cityService;

  public Theater addTheater(TheaterRequest request)
  {
      City city=cityService.getCityById(request.getCityId());
      Theater theater=Theater.builder()
              .name(request.getName())
              .address(request.getAddress())
              .city(city)
              .build();
      return theaterRepository.save(theater);


  }

  public List<Theater> getAllTheater()
  {
      return theaterRepository.findAll();
  }

  public Theater getTheaterById(Long id)
  {
      return theaterRepository.findById(id)
              .orElseThrow(()->new RuntimeException("Theater not found with id: "+id));
  }

  public List<Theater> getTheaterByCity(Long cityId)
  {
      return theaterRepository.findByCity_id(cityId);
  }


}
